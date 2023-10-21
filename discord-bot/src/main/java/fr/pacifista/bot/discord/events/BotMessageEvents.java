package fr.pacifista.bot.discord.events;

import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketMessageClient;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketMessageDTO;
import fr.pacifista.api.support.tickets.client.enums.TicketStatus;
import fr.pacifista.bot.discord.PacifistaBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BotMessageEvents extends ListenerAdapter {
    private final PacifistaBot pacifistaBot;
    private final PacifistaSupportTicketClient ticketClient;
    private final PacifistaSupportTicketMessageClient ticketMessageClient;

    public BotMessageEvents(PacifistaBot pacifistaBot,
                               PacifistaSupportTicketClient ticketClient,
                               PacifistaSupportTicketMessageClient ticketMessageClient) {
        this.pacifistaBot = pacifistaBot;
        this.ticketClient = ticketClient;
        this.ticketMessageClient = ticketMessageClient;
        pacifistaBot.getJda().addEventListener(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Channel channel = event.getChannel();
        Message message = event.getMessage();
        Member member = event.getMember();

        if (channel.getType() == ChannelType.TEXT && channel.getName().startsWith("ticket-")) {
            if (event.getAuthor().isBot()) return;
            if (!channel.getType().equals(ChannelType.TEXT)) return;
            TextChannel ticketChannel = (TextChannel) channel;
            String ticketOwnerId = ticketChannel.getTopic();
            Member ticketOwner = event.getGuild().getMember(UserSnowflake.fromId(ticketOwnerId));

            PacifistaSupportTicketDTO ticketDTO = this.ticketClient.getAll(
                    "0",
                    "1",
                    String.format("createdById:like:%s", ticketOwnerId),
                    ""
                    ).getContent().get(0);

            if (    !ticketDTO.getStatus().equals(TicketStatus.IN_PROGRESS) &&
                    isTicketsMod(member) &&
                    !modAlreadyReplied(ticketChannel)) {
                ticketDTO.setUpdatedAt(Date.from(event.getMessage().getTimeCreated().toInstant()));
                ticketDTO.setStatus(TicketStatus.IN_PROGRESS);
                this.ticketClient.update(ticketDTO);
            }

            PacifistaSupportTicketMessageDTO ticketMessageDTO = new PacifistaSupportTicketMessageDTO();
            ticketMessageDTO.setTicket(ticketDTO);
            ticketMessageDTO.setMessage(message.getContentRaw());
            ticketMessageDTO.setCreatedAt(Date.from(message.getTimeCreated().toInstant()));
            if (message.getTimeEdited() != null) ticketMessageDTO.setUpdatedAt(Date.from(message.getTimeEdited().toInstant()));
            ticketMessageDTO.setWrittenByName(ticketOwner.getUser().getName());
            ticketMessageDTO.setWrittenById(ticketOwnerId);

            this.ticketMessageClient.create(ticketMessageDTO);
        }
    }

    private boolean modAlreadyReplied(TextChannel channel) {
        List<Message> msgHistory = channel.getHistory().getRetrievedHistory();

        for (Message msg : msgHistory.toArray(new Message[0])) {
            if (isTicketsMod(msg.getMember())) return true;
        }

        return false;
    }

    private boolean isTicketsMod(Member member) {
        final String ticketModRoleId = this.pacifistaBot.getBotConfig().getTicketsModRoleID();
        final Role ticketModRole = member.getJDA().getRoleById(ticketModRoleId);
        return member.getRoles().contains(ticketModRole);
    }
}
