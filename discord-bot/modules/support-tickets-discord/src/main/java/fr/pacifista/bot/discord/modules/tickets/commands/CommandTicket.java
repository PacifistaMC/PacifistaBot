package fr.pacifista.bot.discord.modules.tickets.commands;

import fr.pacifista.bot.discord.modules.core.commands.BotCommand;
import fr.pacifista.bot.discord.modules.core.utils.Colors;
import fr.pacifista.bot.discord.modules.tickets.config.BotTicketConfig;
import fr.pacifista.bot.discord.modules.tickets.events.TicketCloseButton;
import fr.pacifista.bot.discord.modules.tickets.events.TicketCreateButton;
import fr.pacifista.bot.discord.modules.tickets.utils.TicketUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandTicket extends BotCommand {
    private final BotTicketConfig botConfig;
    private final TicketCloseButton ticketCloseButton;
    private final TicketCreateButton ticketCreateButton;

    public CommandTicket(JDA jda,
                         BotTicketConfig botConfig,
                         TicketCloseButton ticketCloseButton,
                         TicketCreateButton ticketCreateButton) {
        super(jda, List.of(
                new SubcommandData("close", "Fermer un ticket !"),
                new SubcommandData("sendmessage", "Envoyer le messager permettant de créer son ticket !")
        ));
        this.botConfig = botConfig;
        this.ticketCloseButton = ticketCloseButton;
        this.ticketCreateButton = ticketCreateButton;
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
        if (!TicketUtils.handleTicketChannel(interactionEvent, this.botConfig.getTicketsCategoryId())) return;

        interactionEvent.reply("Êtes vous sûr de vouloir fermer ce ticket ? Il ne sera plus accessible.")
                .addActionRow(this.ticketCloseButton.createButton())
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

        channel.sendMessageEmbeds(embed.build()).addActionRow(this.ticketCreateButton.createButton()).queue();
        interactionEvent.reply("Message envoyé !").setEphemeral(true).queue();
    }
}
