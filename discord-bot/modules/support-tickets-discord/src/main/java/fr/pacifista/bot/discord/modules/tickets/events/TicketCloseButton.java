package fr.pacifista.bot.discord.modules.tickets.events;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.exceptions.ApiNotFoundException;
import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.enums.TicketStatus;
import fr.pacifista.bot.discord.modules.core.events.buttons.ButtonEvent;
import fr.pacifista.bot.discord.modules.core.utils.Colors;
import fr.pacifista.bot.discord.modules.tickets.config.BotTicketConfig;
import fr.pacifista.bot.discord.modules.tickets.utils.TicketUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j(topic = "TicketCloseButton")
@Service
public class TicketCloseButton extends ButtonEvent {
    private final PacifistaSupportTicketClient ticketClient;
    private final BotTicketConfig botConfig;

    public TicketCloseButton(JDA jda,
                             BotTicketConfig botConfig,
                             PacifistaSupportTicketClient ticketClient) {
        super(jda, "ticket-close");
        this.ticketClient = ticketClient;
        this.botConfig = botConfig;
    }

    @Override
    public Button createButton() {
        return Button.danger(getButtonId(), "Fermer le ticket")
                .withEmoji(Emoji.fromUnicode("⚠️"));
    }

    @Override
    public void onButtonEvent(@NotNull ButtonInteractionEvent event) {
        if (event.getChannel() instanceof TextChannel channel && TicketUtils.isTicketChannel(channel, botConfig.getTicketsCategoryId())) {
            event.reply("Fermeture du ticket...").queue();
            final User user = event.getUser();
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

            channel.delete().queue();

            final TextChannel ticketsLogsChannel = event.getGuild().getTextChannelById(botConfig.getTicketsLogsChannelId());
            if (ticketsLogsChannel == null) return;

            final Date archivedDate = Date.from(event.getTimeCreated().toInstant());

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Colors.PACIFISTA_COLOR)
                    .setTitle("Ticket archivé")
                    .setDescription(String.format(
                            "Ticket archivé le <t:%s> par `%s` (`%s`). ID: `%s`",
                            archivedDate.toInstant().getEpochSecond(),
                            user.getName(),
                            user.getId(),
                            ticketId
                    ));

            ticketsLogsChannel.sendMessageEmbeds(embed.build()).queue();
            closeTicket(ticketDTO, archivedDate);
        } else {
            event.reply(":warning: Ce salon n'est pas un ticket !").queue();
        }
    }

    private void closeTicket(final PacifistaSupportTicketDTO ticketDTO, final Date updatedAt) {
        ticketDTO.setStatus(TicketStatus.SOLVED);
        ticketDTO.setUpdatedAt(updatedAt);
        try {
            this.ticketClient.update(ticketDTO);
        } catch (ApiException e) {
            log.error("Impossible de fermer le ticket ID: {}", ticketDTO.getId());
        }
    }
}
