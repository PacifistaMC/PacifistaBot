package fr.pacifista.bot.discord.events;

import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.bot.discord.PacifistaBot;
import fr.pacifista.bot.discord.events.buttons.TicketCloseButton;
import fr.pacifista.bot.discord.events.buttons.TicketCreateButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

@Service
public class BotButtonEvents extends ListenerAdapter {
    private final PacifistaBot pacifistaBot;
    private final PacifistaSupportTicketClient ticketClient;

    public BotButtonEvents(PacifistaBot pacifistaBot, PacifistaSupportTicketClient ticketClient) {
        this.pacifistaBot = pacifistaBot;
        this.ticketClient = ticketClient;
        pacifistaBot.getJda().addEventListener(this);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        final String buttonId = event.getInteraction().getComponentId();

        switch (buttonId) {
            case "ticket-create":
                new TicketCreateButton().onButton(event);
                break;
            case "ticket-close":
                new TicketCloseButton(this.pacifistaBot, this.ticketClient).onButton(event);
                break;
        }
    }
}
