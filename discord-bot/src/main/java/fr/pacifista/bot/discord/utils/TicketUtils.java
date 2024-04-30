package fr.pacifista.bot.discord.utils;

import fr.pacifista.api.support.tickets.client.enums.TicketType;
import fr.pacifista.bot.discord.config.BotConfig;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class TicketUtils {
    private final BotConfig botConfig;

    public TicketUtils(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public void createTicket(@NonNull ModalInteractionEvent event, TicketType ticketType) {
        Category category = event.getJDA().getCategoryById(botConfig.getTicketsCategoryId());
        User ticketOwner = event.getUser();
        TextChannel ticketChannel = category.createTextChannel(String.format("ticket-%s", ticketOwner.getGlobalName()))
                .setTopic(ticketOwner.getId())
                .complete();

        Role modRole = event.getJDA().getRoleById(botConfig.getTicketsModRoleID());
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
                .setTitle(String.format("Ticket de %s (%s)", ticketOwner.getEffectiveName(), ticketOwner.getName()))
                .setDescription("Ton ticket à été crée. Merci de patienter, un modérateur viendra y répondre rapidement.")
                .addField(new MessageEmbed.Field("Type", ticketType.name(), true))
                .addField(new MessageEmbed.Field("Objet", event.getValue("object").getAsString(), true));

        ticketChannel.sendMessageEmbeds(embed.build())
                .queue();
    }
}
