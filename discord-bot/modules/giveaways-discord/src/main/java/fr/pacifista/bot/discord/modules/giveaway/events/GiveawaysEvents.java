package fr.pacifista.bot.discord.modules.giveaway.events;

import fr.pacifista.bot.core.giveaways.GiveawaysManager;
import fr.pacifista.bot.core.giveaways.entities.Giveaway;
import fr.pacifista.bot.core.giveaways.enums.GiveawayType;
import fr.pacifista.bot.discord.modules.core.utils.Colors;
import fr.pacifista.bot.discord.modules.giveaway.config.BotGiveawayConfig;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j(topic = "Giveaways Events")
@Service
public class GiveawaysEvents extends ListenerAdapter {

    private final BotGiveawayConfig botConfig;
    private final GiveawaysManager giveawaysManager;
    private final Random random = new Random();

    public GiveawaysEvents(final JDA jda,
                           final BotGiveawayConfig botConfig,
                           final GiveawaysManager giveawaysManager) {
        jda.addEventListener(this);
        this.botConfig = botConfig;
        this.giveawaysManager = giveawaysManager;
    }

    @Override
    public void onStringSelectInteraction(@NonNull StringSelectInteractionEvent event) {
        if (event.getInteraction().getComponentId().equals("giveaway-roll")) {
            this.rollGiveaway(event);
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        final String interactionId = event.getModalId();
        final String modalId = interactionId.split(":")[0];

        if (modalId.equals("giveaway-create")) {
            this.createGiveawayFromModal(event);
        }
    }

    private void createGiveawayFromModal(ModalInteractionEvent event) {
        final TextChannel channel = event.getGuild().getTextChannelById(this.botConfig.getGiveawaysChannelId());

        if (channel == null) {
            log.error("Impossible de récupérer le salon des giveaways.");
            return;
        }

        Giveaway giveaway = new Giveaway();
        giveaway.setGiveawayType(GiveawayType.DISCORD);
        giveaway.setGiveawayId(UUID.randomUUID());
        giveaway.setCreatedAt(Date.from(event.getInteraction().getTimeCreated().toInstant()));
        giveaway.setPrize(event.getValue("giveaway-prize").getAsString());
        giveaway.setWinners(Integer.parseInt(event.getValue("giveaway-winners").getAsString()));
        giveaway.setPacifistaCommandToSend(event.getValue("giveaway-pacifista-command").getAsString());

        MessageEmbed embedBuilder = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle(String.format("Giveaway: %s", giveaway.getPrize()))
                .setDescription(String.format(
                        "Tente de gagner **%s** en réagissant à ce message avec la réaction 🎁 !",
                        giveaway.getPrize()))
                .setFooter("Pacifista - Giveaway", event.getJDA().getSelfUser().getAvatarUrl())
                .build();

        channel.sendMessage(String.format("<@&%s>",this.botConfig.getGiveawaysRoleId())).queue();
        final Message message = channel.sendMessageEmbeds(embedBuilder).complete();

        message.addReaction(Emoji.fromUnicode("🎁")).queue();

        giveaway.setDiscordMessageId(message.getId());
        this.giveawaysManager.createGiveaway(giveaway);

        event.reply("Succès !").setEphemeral(true).queue();
    }

    private void rollGiveaway(@NonNull StringSelectInteractionEvent event) {
        final String giveawaysChannelId = this.botConfig.getGiveawaysChannelId();
        final TextChannel giveawaysChannel = event.getGuild().getTextChannelById(giveawaysChannelId);

        if (giveawaysChannel == null) {
            log.error("Impossible de récupérer le salon des giveaways.");
            return;
        }

        final UUID giveawayId = UUID.fromString(event.getValues().get(0));
        final Giveaway giveaway = this.giveawaysManager.getGiveawayById(giveawayId);
        final List<String> participantsIds = giveaway.getParticipantsIds();

        if (giveaway.getWinners() > participantsIds.size()) {
            event.reply("Il y a plus de gagnants que de participants.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        final List<String> winnersIds = this.rollWinners(giveaway);
        final List<String> winnerTags = winnersIds.stream()
                .map(id -> "<@" + id + ">")
                .toList();

        final String message = String.format(
                "Bravo à %s qui remporte%s **%s** !",
                String.join(", ", winnerTags),
                giveaway.getWinners() > 1 ? "nt" : "",
                giveaway.getPrize()
        );

        final MessageEmbed embedBuilder = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle(String.format("Giveaway: %s", giveaway.getPrize()))
                .setDescription(String.format(
                        "Gagnant%s: %s",
                        giveaway.getWinners() > 1 ? "s" : "",
                        String.join(", ", winnerTags)
                ))
                .setFooter("Pacifista - Giveaway", event.getJDA().getSelfUser().getAvatarUrl())
                .build();

        this.giveawaysManager.deleteGiveaway(giveawayId);

        final Message giveawayMessage = giveawaysChannel.retrieveMessageById(giveaway.getDiscordMessageId()).complete();
        giveawayMessage
                .editMessageEmbeds(embedBuilder)
                .queue();

        giveawayMessage.reply(message).queue();

        event.editMessage("Succès !")
                .setComponents()
                .queue();
    }

    private List<String> rollWinners(Giveaway giveaway) {
        List<String> winners = new ArrayList<>();

        while (winners.size() < giveaway.getWinners()) {
            int winnerIndex = this.random.nextInt(0, giveaway.getParticipantsIds().size());
            String winnerId = giveaway.getParticipantsIds().get(winnerIndex);

            winners.add(winnerId);
            giveaway.removeParticipant(winnerId);
        }

        return winners;
    }

}
