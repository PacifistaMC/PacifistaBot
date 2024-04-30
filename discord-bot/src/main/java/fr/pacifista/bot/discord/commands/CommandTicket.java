package fr.pacifista.bot.discord.commands;

import fr.pacifista.bot.discord.config.BotConfig;
import fr.pacifista.bot.discord.utils.Colors;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandTicket extends Command {
    private final BotConfig botConfig;

    public CommandTicket(JDA jda, BotConfig botConfig) {
        super(jda, List.of(
                new SubcommandData("close", "Fermer un ticket !"),
                new SubcommandData("sendmessage", "Envoyer le messager permettant de créer son ticket !")
        ));
        this.botConfig = botConfig;
    }

    @Override
    public String getCommandName() {
        return "ticket";
    }

    @Override
    public String getCommandDescription() {
        return "Commande liée aux tickets !";
    }

    @Override
    public DefaultMemberPermissions getCommandPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL);
    }

    @Override
    public void onCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        if (interactionEvent.getSubcommandName() == null) return;

        switch (interactionEvent.getSubcommandName()) {
            case "close":
                closeTicket(interactionEvent);
                break;
            case "sendmessage":
                sendTicketMessage(interactionEvent);
                break;
        }
    }

    private void closeTicket(@NonNull SlashCommandInteractionEvent interactionEvent) {
        Channel channel = interactionEvent.getChannel();

        if (channel.getType() != ChannelType.TEXT || !channel.getName().contains("ticket-")) {
            interactionEvent.reply(":warning: Ce salon n'est pas un ticket !").queue();
            return;
        }

        Button button = Button.danger("ticket-close", "Fermer le ticket")
                .withEmoji(Emoji.fromUnicode("⚠️"));

        interactionEvent.reply("Êtes vous sûr de vouloir fermer ce ticket ? Il ne sera plus accessible.")
                .addActionRow(button)
                .queue();
    }

    private void sendTicketMessage(@NonNull SlashCommandInteractionEvent interactionEvent) {
        if (interactionEvent.getGuild() == null) return;
        final TextChannel channel = interactionEvent.getGuild().getTextChannelById(botConfig.getTicketsChannelId());
        if (channel == null) {
            interactionEvent.reply(":warning: Impossible de récupérer le salon des tickets.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle("🎫 Tickets")
                .setDescription("Besoin de nous contacter ? Clique sur le bouton ci-dessous pour créer un ticket !");

        Button button = Button.primary("ticket-create", "Créer un ticket");

        channel.sendMessageEmbeds(embed.build()).addActionRow(button).queue();
        interactionEvent.reply("Message envoyé !").setEphemeral(true).queue();
    }
}
