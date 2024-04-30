package fr.pacifista.bot.discord.events;

import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.enums.TicketCreationSource;
import fr.pacifista.api.support.tickets.client.enums.TicketStatus;
import fr.pacifista.api.support.tickets.client.enums.TicketType;
import fr.pacifista.bot.core.GiveawaysManager;
import fr.pacifista.bot.discord.config.BotConfig;
import fr.pacifista.bot.discord.utils.GiveawaysUtils;
import fr.pacifista.bot.discord.utils.TicketUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BotModalEvents extends ListenerAdapter {
    private final PacifistaSupportTicketClient ticketClient;
    private final GiveawaysManager giveawaysManager;
    private final BotConfig botConfig;
    private final TicketUtils ticketUtils;
    private final JDA jda;

    public BotModalEvents(JDA jda,
                          BotConfig botConfig,
                          TicketUtils ticketUtils,
                          PacifistaSupportTicketClient ticketClient,
                          GiveawaysManager giveawaysManager) {
        this.jda = jda;
        this.ticketClient = ticketClient;
        this.ticketUtils = ticketUtils;
        this.giveawaysManager = giveawaysManager;
        this.botConfig = botConfig;
        jda.addEventListener(this);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String interactionId = event.getModalId();
        String modalId = interactionId.split(",")[0];
        String arg = null;
        if (interactionId.contains(",")) arg = interactionId.split(",")[1];

        User user = event.getUser();

        if (modalId.equals("ticket-create")) {
            if (arg == null) return;
            TicketType ticketType = TicketType.valueOf(arg.toUpperCase());
            String object = event.getValue("object").getAsString();

            PacifistaSupportTicketDTO ticketDTO = new PacifistaSupportTicketDTO();
            ticketDTO.setCreatedAt(new Date());
            ticketDTO.setCreatedById(user.getId());
            ticketDTO.setCreatedByName(user.getName());
            ticketDTO.setCreationSource(TicketCreationSource.DISCORD);
            ticketDTO.setType(ticketType);
            ticketDTO.setObject(object);
            ticketDTO.setStatus(TicketStatus.CREATED);

            ticketUtils.createTicket(event, ticketType);
            this.ticketClient.create(ticketDTO);
        } else if (modalId.equals("giveaway-create")) {
            new GiveawaysUtils(this.jda, this.botConfig, this.giveawaysManager).createGiveawayFromModal(event);
        }
    }
}
