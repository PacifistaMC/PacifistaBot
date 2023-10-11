package fr.pacifista.bot.discord.events.buttons;

import fr.pacifista.bot.discord.config.Config;
import fr.pacifista.bot.discord.utils.Colors;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketCloseButton extends Button {
    final Config botConfig;

    public TicketCloseButton(Config botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void onButton(@NonNull ButtonInteractionEvent event) {
        Role ticketModRole = event.getJDA().getRoleById(this.botConfig.getTicketsModRoleID());
        Channel channel = event.getChannel();
        Member member = event.getMember();

        if (channel.getType() != ChannelType.TEXT || !channel.getName().contains("ticket-")) {
            event.reply(":warning: Ce salon n'est pas un ticket !").queue();
        }

        TextChannel ticketChannel = (TextChannel) channel;
        Category ticketsLogsCategory = event.getJDA().getCategoryById(this.botConfig.getTicketsLogsCategoryId());

        if (!member.getRoles().contains(ticketModRole)) {
            ticketChannel.getManager().removePermissionOverride(event.getMember().getIdLong()).queue();
        }

        event.getMessage().delete().queue();

        ticketChannel.getManager().setParent(ticketsLogsCategory).queue();

        String archiveFormattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle("Ticket archivé")
                .setDescription(String.format("Ticket archivé le `%s` par `%s` (`%s`)", archiveFormattedDate, member.getUser().getName(), member.getUser().getId()));

        ticketChannel.sendMessageEmbeds(embed.build()).queue();
    }
}
