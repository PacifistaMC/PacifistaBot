package fr.pacifista.bot.discord.modules.tickets.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketUtils {
    public static boolean isTicketChannel(final TextChannel channel, final String ticketCategoryId) {
        return channel.getParentCategoryId() != null && channel.getParentCategoryId().equals(ticketCategoryId);
    }
}
