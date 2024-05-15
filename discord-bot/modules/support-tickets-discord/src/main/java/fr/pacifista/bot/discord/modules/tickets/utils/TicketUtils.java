package fr.pacifista.bot.discord.modules.tickets.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketUtils {

    public static boolean handleTicketChannel(SlashCommandInteractionEvent interactionEvent, String ticketCategoryId) {
        if (!(interactionEvent.getChannel() instanceof TextChannel channel && isTicketChannel(channel, ticketCategoryId))) {
            interactionEvent.reply(":warning: Ce salon n'est pas un ticket !").setEphemeral(true).queue();
            return false;
        }
        return true;
    }

    public static boolean isTicketChannel(TextChannel channel, String ticketCategoryId) {
        return channel.getParentCategoryId() != null && channel.getParentCategoryId().equals(ticketCategoryId);
    }
}
