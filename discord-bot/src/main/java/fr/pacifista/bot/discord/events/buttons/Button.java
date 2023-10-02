package fr.pacifista.bot.discord.events.buttons;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@Getter
public abstract class Button {
    void onButton(@NonNull ButtonInteractionEvent event) {}
}
