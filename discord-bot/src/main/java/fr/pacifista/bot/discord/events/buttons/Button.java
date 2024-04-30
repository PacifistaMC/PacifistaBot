package fr.pacifista.bot.discord.events.buttons;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface Button {
    void onButton(@NonNull ButtonInteractionEvent event);
}
