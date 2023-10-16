package fr.pacifista.bot.discord.events;

import fr.pacifista.bot.discord.PacifistaBot;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Service;

@Service
public class BotSelectEvents extends ListenerAdapter {
    public BotSelectEvents(PacifistaBot pacifistaBot) {
        pacifistaBot.getJda().addEventListener(this);
    }

    @Override
    public void onStringSelectInteraction(@NonNull StringSelectInteractionEvent event) {
        String selectId = event.getInteraction().getComponentId();

        if (selectId.equals("ticket-create")) {
            String ticketType = event.getValues().get(0);

            TextInput object = TextInput.create("object", "Objet", TextInputStyle.SHORT)
                    .setPlaceholder("Objet du ticket")
                    .setMinLength(10)
                    .setMaxLength(100)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create(String.format("ticket-create,%s", ticketType), "Crée un ticket")
                    .addActionRow(object)
                    .build();

            event.replyModal(modal).queue();
        }
    }
}
