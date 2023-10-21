package fr.pacifista.bot.discord.utils;

import fr.pacifista.bot.core.GiveawaysManager;
import fr.pacifista.bot.core.entities.giveaways.Giveaway;
import fr.pacifista.bot.core.entities.giveaways.enums.GiveawayType;
import fr.pacifista.bot.discord.PacifistaBot;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j(topic = "Giveaways Utils")
public class GiveawaysUtils {
    private final PacifistaBot pacifistaBot;
    private final GiveawaysManager giveawaysManager;

    public GiveawaysUtils(PacifistaBot pacifistaBot, GiveawaysManager giveawaysManager) {
        this.pacifistaBot = pacifistaBot;
        this.giveawaysManager = giveawaysManager;
    }

    public void createGiveawayFromModal(ModalInteractionEvent event) {
        String giveawaysChannelId = this.pacifistaBot.getBotConfig().getGiveawaysChannelId();
        TextChannel channel = event.getJDA().getTextChannelById(giveawaysChannelId);

        if (channel == null || channel.getType() != ChannelType.TEXT) {
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

        channel.sendMessage(String.format("<@&%s>", this.pacifistaBot.getBotConfig().getGiveawaysRoleId())).queue();
        Message message = channel.sendMessageEmbeds(embedBuilder).complete();

        message.addReaction(Emoji.fromUnicode("🎁")).queue();

        giveaway.setDiscordMessageId(message.getId());
        this.giveawaysManager.createGiveaway(giveaway);

        event.reply("Succès !").setEphemeral(true).queue();
    }

    public void rollGiveaway(@NonNull StringSelectInteractionEvent event) {
        String giveawaysChannelId = this.pacifistaBot.getBotConfig().getGiveawaysChannelId();
        TextChannel giveawaysChannel = event.getJDA().getTextChannelById(giveawaysChannelId);

        if (giveawaysChannel == null || giveawaysChannel.getType() != ChannelType.TEXT) {
            log.error("Impossible de récupérer le salon des giveaways.");
            return;
        }

        UUID giveawayId = UUID.fromString(event.getValues().get(0));
        Giveaway giveaway = this.giveawaysManager.getGiveawayById(giveawayId);
        List<String> participantsIds = giveaway.getParticipantsIds();

        if (giveaway.getWinners() > participantsIds.size()) {
            event.reply("Il y a plus de gagnants que de participants.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        List<String> winnersIds = this.rollWinners(giveaway);
        List<String> winnerTags = winnersIds.stream()
                .map(id -> "<@" + id + ">")
                .collect(Collectors.toList());

        String message = String.format(
                "Bravo à %s qui remporte%s **%s** !",
                String.join(", ", winnerTags),
                giveaway.getWinners() > 1 ? "nt" : "",
                giveaway.getPrize()
        );

        MessageEmbed embedBuilder = new EmbedBuilder()
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

        Message giveawayMessage = giveawaysChannel.retrieveMessageById(giveaway.getDiscordMessageId()).complete();
        giveawayMessage
                .editMessageEmbeds(embedBuilder)
                .queue();

        giveawayMessage.reply(message).queue();

        event.editMessage("Succès !")
                .setComponents()
                .queue();
    }

    private List<String> rollWinners(Giveaway giveaway) {
        Random random = new Random();
        List<String> winners = new ArrayList<>();

        while (winners.size() < giveaway.getWinners()) {
            int winnerIndex = random.nextInt(0, giveaway.getParticipantsIds().size());
            String winnerId = giveaway.getParticipantsIds().get(winnerIndex);

            winners.add(winnerId);
            giveaway.removeParticipant(winnerId);
        }

        return winners;
    }
}
