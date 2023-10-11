package fr.pacifista.bot.discord.commands;

import fr.pacifista.bot.discord.PacifistaBot;
import fr.pacifista.bot.discord.utils.Colors;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

@Service
public class CommandSendTicketMessage extends Command {
    private final PacifistaBot pacifistaBot;

    public CommandSendTicketMessage(PacifistaBot pacifistaBot) {
        super(pacifistaBot.getJda());
        this.pacifistaBot = pacifistaBot;
    }

    @Override
    public String getCommandName() {
        return "sendticketmessage";
    }

    @Override
    public String getCommandDescription() {
        return "Envoie le message permettant de créer un ticket.";
    }

    @Override
    public DefaultMemberPermissions getCommandPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.ALL_CHANNEL_PERMISSIONS);
    }

    @Override
    public void onCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        TextChannel channel = interactionEvent.getGuild().getTextChannelById(this.pacifistaBot.getBotConfig().getTicketsChannelId());

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle("🎫 Tickets")
                .setDescription("Besoin de nous contacter ? Clique sur le bouton ci-dessous pour créer un ticket !");

        Button button = Button.primary("ticket-create", "Créer un ticket");

        channel.sendMessageEmbeds(embed.build()).addActionRow(button).queue();
        interactionEvent.reply("Message envoyé !").setEphemeral(true).queue();
    }

}
