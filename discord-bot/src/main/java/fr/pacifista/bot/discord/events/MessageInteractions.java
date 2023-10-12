package fr.pacifista.bot.discord.events;

import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketMessageClient;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketMessageDTO;
import fr.pacifista.bot.discord.PacifistaBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MessageInteractions extends ListenerAdapter {
    private final PacifistaSupportTicketClient ticketClient;
    private final PacifistaSupportTicketMessageClient ticketMessageClient;

    public MessageInteractions(PacifistaBot pacifistaBot,
                               PacifistaSupportTicketClient ticketClient,
                               PacifistaSupportTicketMessageClient ticketMessageClient) {
        this.ticketClient = ticketClient;
        this.ticketMessageClient = ticketMessageClient;
        pacifistaBot.getJda().addEventListener(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Channel channel = event.getChannel();
        Message message = event.getMessage();

        if (channel.getType() == ChannelType.TEXT && channel.getName().startsWith("ticket-")) {
            String ticketOwnerUsername = channel.getName().split("-")[1];
            Member ticketOwner = event.getGuild().getMembersByName(ticketOwnerUsername, true).getFirst();
            PacifistaSupportTicketDTO ticketDTO = this.ticketClient.getAll(
                    "0",
                    "1",
                    String.format("createdById:like:%s", ticketOwner.getId()),
                    ""
                    ).getContent().getFirst();

            PacifistaSupportTicketMessageDTO ticketMessageDTO = new PacifistaSupportTicketMessageDTO();
            ticketMessageDTO.setTicket(ticketDTO);
            ticketMessageDTO.setMessage(message.getContentRaw());
            ticketMessageDTO.setCreatedAt(Date.from(message.getTimeCreated().toInstant()));
            if (message.getTimeEdited() != null) ticketMessageDTO.setUpdatedAt(Date.from(message.getTimeEdited().toInstant()));
            ticketMessageDTO.setWrittenByName(ticketOwnerUsername);
            ticketMessageDTO.setWrittenById(ticketOwner.getId());

            this.ticketMessageClient.create(ticketMessageDTO);
        }
    }
}
