package fr.pacifista.bot.discord.events;

import fr.pacifista.api.support.tickets.client.clients.PacifistaSupportTicketClient;
import fr.pacifista.bot.discord.config.BotConfig;
import fr.pacifista.bot.discord.events.buttons.TicketCloseButton;
import fr.pacifista.bot.discord.events.buttons.TicketCreateButton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

@Service
public class BotButtonEvents extends ListenerAdapter {
    private final PacifistaSupportTicketClient ticketClient;
    private final BotConfig botConfig;

    public BotButtonEvents(BotConfig botConfig, JDA jda, PacifistaSupportTicketClient ticketClient) {
        this.ticketClient = ticketClient;
        this.botConfig = botConfig;
        jda.addEventListener(this);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        final String buttonId = event.getInteraction().getComponentId();

        switch (buttonId) {
            case "ticket-create":
                new TicketCreateButton().onButton(event);
                break;
            case "ticket-close":
                new TicketCloseButton(this.botConfig, this.ticketClient).onButton(event);
                break;
        }
    }
}
