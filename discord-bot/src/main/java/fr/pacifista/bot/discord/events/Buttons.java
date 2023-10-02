package fr.pacifista.bot.discord.events;

import fr.pacifista.api.support.tickets.client.enums.TicketType;
import fr.pacifista.bot.discord.config.Config;
import fr.pacifista.bot.discord.events.buttons.TicketClose;
import fr.pacifista.bot.discord.events.buttons.TicketCreate;
import fr.pacifista.bot.discord.utils.Colors;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.EnumSet;

public class Buttons extends ListenerAdapter {
    private final Config botConfig;

    public Buttons(Config botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        final String buttonId = event.getInteraction().getComponentId();

        switch (buttonId) {
            case "ticket-create":
                new TicketCreate().onButton(event);
                break;
            case "ticket-close":
                new TicketClose(this.botConfig).onButton(event);
                break;
        }
    }

    @Override
    public void onStringSelectInteraction(@NonNull StringSelectInteractionEvent event) {
        final String selectId = event.getInteraction().getComponentId();

        if (selectId.equals("ticket-create")) {
            createTicket(event);
        }
    }

    public void createTicket(@NonNull StringSelectInteractionEvent event) {
        Category category = event.getJDA().getCategoryById(this.botConfig.getTicketsCategoryId());

        TextChannel ticketChannel =  category.createTextChannel(String.format("ticket-%s", event.getMember().getUser().getGlobalName())).complete();

        loadTicket(event, ticketChannel);
    }

    public void loadTicket(@NonNull StringSelectInteractionEvent event, TextChannel textChannel) {
        Role modRole = event.getJDA().getRoleById(this.botConfig.getTicketsModRoleID());
        Role everyoneRole = event.getGuild().getRolesByName("@everyone", true).get(0);
        TicketType ticketType = TicketType.valueOf(event.getValues().get(0).toUpperCase());

        textChannel.getManager()
                .putPermissionOverride(event.getMember(), EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .putPermissionOverride(modRole, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
                .putPermissionOverride(everyoneRole, null, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND))
                .queue();

        event.editMessage(String.format("Ticket crée avec succès ! <#%s>", textChannel.getId()))
                .setComponents()
                .queue();

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle(String.format("Ticket de %s (%s)", event.getMember().getUser().getEffectiveName(), event.getMember().getUser().getName()))
                .setDescription("Ton ticket à été crée. Merci de patienter, un modérateur viendra y répondre rapidement.")
                .addField(new MessageEmbed.Field("Type", ticketType.name(), true));

        textChannel.sendMessageEmbeds(embed.build())
                .queue();
    }
}
