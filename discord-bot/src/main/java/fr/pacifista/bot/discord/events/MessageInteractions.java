package fr.pacifista.bot.discord.events;

import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketMessageDTO;
import fr.pacifista.bot.discord.api.PacifistaTicketClient;
import fr.pacifista.bot.discord.api.PacifistaTicketMessageClient;
import fr.pacifista.bot.discord.config.Config;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class MessageInteractions extends ListenerAdapter {
    private final Config config;
    PacifistaTicketMessageClient ticketMessageClient;

    public MessageInteractions(Config config) {
        this.config = config;
        this.ticketMessageClient = new PacifistaTicketMessageClient(config);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Channel channel = event.getChannel();
        Message message = event.getMessage();

        if (channel.getType() == ChannelType.TEXT && channel.getName().startsWith("ticket-")) {
            String ticketOwnerUsername = channel.getName().split("-")[1];
            Member ticketOwner = event.getGuild().getMembersByName(ticketOwnerUsername, true).getFirst();
            PacifistaTicketClient ticketClient = new PacifistaTicketClient(this.config);
            PacifistaSupportTicketDTO ticketDTO = ticketClient.getAll("0", "1", String.format("createdById:like:%s", ticketOwner.getId()), "").getContent().getFirst();

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

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        Channel channel = event.getChannel();
        String ticketOwnerUsername = channel.getName().split("-")[1];
        Member ticketOwner = event.getGuild().getMembersByName(ticketOwnerUsername, true).getFirst();
        Message message = event.getMessage();

        if (channel.getType() == ChannelType.TEXT && channel.getName().startsWith("ticket-")) {
            PacifistaSupportTicketMessageDTO ticketMessageDTO = this.ticketMessageClient.getAll(
                    "0",
                    "0",
                    String.format("createdById:like:%s", ticketOwner.getId()),
                    "createdAt:desc"
            ).getContent().getFirst();

            ticketMessageDTO.setUpdatedAt(Date.from(message.getTimeEdited().toInstant()));
            ticketMessageDTO.setMessage(message.getContentRaw());

            this.ticketMessageClient.update(ticketMessageDTO);
        }
    }
}
