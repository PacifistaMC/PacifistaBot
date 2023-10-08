package fr.pacifista.bot.discord.events;

import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.api.support.tickets.client.enums.TicketCreationSource;
import fr.pacifista.api.support.tickets.client.enums.TicketStatus;
import fr.pacifista.api.support.tickets.client.enums.TicketType;
import fr.pacifista.bot.discord.api.PacifistaTicketClient;
import fr.pacifista.bot.discord.config.Config;
import fr.pacifista.bot.discord.events.buttons.TicketClose;
import fr.pacifista.bot.discord.events.buttons.TicketCreate;
import fr.pacifista.bot.discord.utils.TicketUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TicketInteractions extends ListenerAdapter {
    private final Config botConfig;

    public TicketInteractions(Config botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        final String buttonId = event.getInteraction().getComponentId();

        switch (buttonId) {
            case "ticket-create":
                new TicketCreate().onButton(event);
                break;
            case "ticket-close":
                new TicketClose(this.botConfig).onButton(event);
                break;
        }
    }

    @Override
    public void onStringSelectInteraction(@NonNull StringSelectInteractionEvent event) {
        String selectId = event.getInteraction().getComponentId();
        String ticketType = event.getValues().get(0);

        TextInput object = TextInput.create("object", "Objet", TextInputStyle.SHORT)
                .setPlaceholder("Objet du ticket")
                .setMinLength(10)
                .setMaxLength(100)
                .setRequired(true)
                .build();

        if (selectId.equals("ticket-create")) {
            Modal modal = Modal.create(String.format("ticket-create,%s", ticketType), "Crée un ticket")
                    .addActionRow(object)
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        TicketUtils ticketUtils = new TicketUtils(this.botConfig);
        PacifistaTicketClient ticketClient = new PacifistaTicketClient(this.botConfig);
        String interactionId = event.getModalId();
        String modalId = interactionId.split(",")[0];
        String arg = interactionId.split(",")[1];

        User user = event.getUser();

        if (modalId.equals("ticket-create")) {
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
            ticketClient.create(ticketDTO);
        }
    }
}
