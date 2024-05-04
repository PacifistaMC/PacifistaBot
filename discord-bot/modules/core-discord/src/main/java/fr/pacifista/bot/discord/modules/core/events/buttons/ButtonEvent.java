package fr.pacifista.bot.discord.modules.core.events.buttons;

import fr.pacifista.bot.core.exceptions.PacifistaBotException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

@Getter
@Slf4j(topic = "ButtonEvent")
public abstract class ButtonEvent extends ListenerAdapter {

    private final String buttonId;

    protected ButtonEvent(final JDA jda,
                final String buttonId) {
        this.buttonId = buttonId;
        jda.addEventListener(this);
    }

    public abstract void onButtonEvent(@NotNull ButtonInteractionEvent event) throws PacifistaBotException;
    public abstract Button createButton();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        try {
            if (event.getInteraction().getComponentId().equals(this.buttonId)) {
                this.onButtonEvent(event);
            }
        } catch (PacifistaBotException e) {
            log.error("An error occurred while processing a button event", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing a button event", new PacifistaBotException(e));
        }
    }
}
