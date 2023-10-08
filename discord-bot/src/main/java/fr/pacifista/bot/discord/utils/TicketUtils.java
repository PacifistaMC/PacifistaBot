package fr.pacifista.bot.discord.utils;

import fr.pacifista.api.support.tickets.client.enums.TicketType;
import fr.pacifista.bot.discord.config.Config;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class TicketUtils {
    private final JDA jda;
    private final Config config;

    public TicketUtils(final JDA jda, Config config) {
        this.jda = jda;
        this.config = config;

        sendTicketMessage();
    }

    public void sendTicketMessage() {
        TextChannel channel = jda.getTextChannelById(this.config.getTicketsChannelId());

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle("🎫 Tickets")
                .setDescription("Besoin de nous contacter ? Clique sur le bouton ci-dessous pour créer un ticket !");

        Button button = Button.primary("ticket-create", "Créer un ticket");

        channel.sendMessageEmbeds(embed.build()).addActionRow(button).queue();
    }

    public void createTicket(@NonNull ModalInteractionEvent event, TicketType ticketType) {
        Category category = event.getJDA().getCategoryById(this.config.getTicketsCategoryId());
        TextChannel ticketChannel = category.createTextChannel(String.format("ticket-%s", event.getUser().getGlobalName())).complete();

        Role modRole = event.getJDA().getRoleById(this.config.getTicketsModRoleID());
        Role everyoneRole = event.getGuild().getRolesByName("@everyone", true).get(0);

        ticketChannel.getManager()
                .putPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .putPermissionOverride(modRole, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .putPermissionOverride(everyoneRole, null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND))
                .queue();

        event.editMessage(String.format("Ticket crée avec succès ! <#%s>", ticketChannel.getId()))
                .setComponents()
                .queue();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle(String.format("Ticket de %s (%s)", event.getMember().getUser().getEffectiveName(), event.getMember().getUser().getName()))
                .setDescription("Ton ticket à été crée. Merci de patienter, un modérateur viendra y répondre rapidement.")
                .addField(new MessageEmbed.Field("Type", ticketType.name(), true))
                .addField(new MessageEmbed.Field("Objet", event.getValue("object").getAsString(), true));

        ticketChannel.sendMessageEmbeds(embed.build())
                .queue();
    }
}
