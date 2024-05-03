package fr.pacifista.bot.discord.modules.tickets.events;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.exceptions.ApiNotFoundException;
import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketMessageClient;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketMessageDTO;
import fr.pacifista.api.support.tickets.client.enums.TicketStatus;
import fr.pacifista.bot.discord.modules.tickets.config.BotTicketConfig;
import fr.pacifista.bot.discord.modules.tickets.utils.TicketUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j(topic = "TicketsMessageEvents")
@Service
public class MessagesEvents extends ListenerAdapter {

    private final PacifistaSupportTicketMessageClient ticketMessageClient;
    private final PacifistaSupportTicketClient ticketClient;
    private final BotTicketConfig botConfig;

    public MessagesEvents(JDA jda,
                            BotTicketConfig botConfig,
                            PacifistaSupportTicketClient ticketClient,
                            PacifistaSupportTicketMessageClient ticketMessageClient) {
        this.ticketClient = ticketClient;
        this.botConfig = botConfig;
        this.ticketMessageClient = ticketMessageClient;
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        final Message message = event.getMessage();
        final Member member = event.getMember();

        if (event.getChannel() instanceof TextChannel channel && TicketUtils.isTicketChannel(channel, botConfig.getTicketsCategoryId())) {
            final String ticketId = channel.getTopic();
            PacifistaSupportTicketDTO ticketDTO = null;

            try {
                ticketDTO = this.ticketClient.findById(ticketId);
            } catch (ApiNotFoundException e) {
                channel.sendMessage(":warning: Impossible de récupérer le ticket.").queue();
                log.error("Impossible de récupérer le ticket: ID: {}, ChannelID: {}", ticketId, channel.getId());
            } catch (ApiException e) {
                channel.sendMessage(":warning: Une erreur API est survenue lors de la récupération du ticket.").queue();
                log.error("Impossible de récupérer le ticket", e);
            }

            if (ticketDTO == null) return;
            final Member ticketOwner = event.getGuild().retrieveMember(UserSnowflake.fromId(ticketDTO.getCreatedById())).complete();

            if (ticketDTO.getStatus().equals(TicketStatus.CREATED)
                    && isTicketsMod(member) &&
                    !modAlreadyReplied(channel)) {
                ticketDTO.setUpdatedAt(Date.from(event.getMessage().getTimeCreated().toInstant()));
                ticketDTO.setStatus(TicketStatus.IN_PROGRESS);

                try {
                    this.ticketClient.update(ticketDTO);
                } catch (ApiException e) {
                    channel.sendMessage(":warning: Impossible de mettre à jour le ticket.").queue();
                    log.error("Impossible d'update le ticket", e);
                }
            }

            PacifistaSupportTicketMessageDTO ticketMessageDTO = new PacifistaSupportTicketMessageDTO();
            ticketMessageDTO.setTicket(ticketDTO);
            ticketMessageDTO.setMessage(message.getContentRaw());
            ticketMessageDTO.setCreatedAt(Date.from(message.getTimeCreated().toInstant()));
            if (message.getTimeEdited() != null) ticketMessageDTO.setUpdatedAt(Date.from(message.getTimeEdited().toInstant()));
            ticketMessageDTO.setWrittenByName(ticketOwner.getUser().getName());
            ticketMessageDTO.setWrittenById(ticketOwner.getId());

            try {
                this.ticketMessageClient.create(ticketMessageDTO);
            } catch (ApiException e) {
                channel.sendMessage(":warning: Impossible de créer le message du ticket.").queue();
                log.error("Impossible de créer le ticket message", e);
            }
        }
    }

    private boolean modAlreadyReplied(TextChannel channel) {
        List<Message> msgHistory = channel.getHistory().getRetrievedHistory();

        for (Message msg : msgHistory) {
            if (msg.getMember() == null || msg.getMember().getUser().isBot()) continue;
            if (isTicketsMod(msg.getMember())) return true;
        }

        return false;
    }

    private boolean isTicketsMod(final Member member) {
        final String ticketModRoleId = this.botConfig.getTicketsModRoleId();
        final Role ticketModRole = member.getJDA().getRoleById(ticketModRoleId);
        return member.getRoles().contains(ticketModRole);
    }

}
