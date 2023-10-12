package fr.pacifista.bot.discord.events.buttons;

import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.enums.TicketStatus;
import fr.pacifista.bot.discord.PacifistaBot;
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
    private final PacifistaBot pacifistaBot;
    private final PacifistaSupportTicketClient ticketClient;

    public TicketCloseButton(PacifistaBot pacifistaBot,
                             PacifistaSupportTicketClient ticketClient) {
        this.pacifistaBot = pacifistaBot;
        this.ticketClient = ticketClient;
    }

    @Override
    public void onButton(@NonNull ButtonInteractionEvent event) {
        Role ticketModRole = event.getJDA().getRoleById(this.pacifistaBot.getBotConfig().getTicketsModRoleID());
        Channel channel = event.getChannel();
        Member member = event.getMember();
        String ticketOwnerUsername = channel.getName().split("-")[1];
        Member ticketOwner = event.getGuild().getMembersByName(ticketOwnerUsername, true).getFirst();

        if (channel.getType() != ChannelType.TEXT || !channel.getName().contains("ticket-")) {
            event.reply(":warning: Ce salon n'est pas un ticket !").queue();
        }

        TextChannel ticketChannel = (TextChannel) channel;
        Category ticketsLogsCategory = event.getJDA().getCategoryById(this.pacifistaBot.getBotConfig().getTicketsLogsCategoryId());

        if (!member.getRoles().contains(ticketModRole)) {
            ticketChannel.getManager().removePermissionOverride(event.getMember().getIdLong()).queue();
        }

        event.getMessage().delete().queue();

        ticketChannel.getManager().setParent(ticketsLogsCategory).queue();

        Date archivedDate = Date.from(event.getTimeCreated().toInstant());
        String archiveFormattedDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(archivedDate);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle("Ticket archivé")
                .setDescription(String.format("Ticket archivé le `%s` par `%s` (`%s`)", archiveFormattedDate, member.getUser().getName(), member.getUser().getId()));

        ticketChannel.sendMessageEmbeds(embed.build()).queue();
        closeTicket(ticketOwner.getId(), archivedDate);
    }

    private void closeTicket(String ticketOwnerId, Date updatedAt) {
        PacifistaSupportTicketDTO ticketDTO = this.ticketClient.getAll(
                "0",
                "1",
                String.format("createdById:like:%s", ticketOwnerId),
                "").getContent().getFirst();

        ticketDTO.setStatus(TicketStatus.SOLVED);
        ticketDTO.setUpdatedAt(updatedAt);
        this.ticketClient.update(ticketDTO);
    }
}
