package fr.pacifista.bot.discord.modules.tickets.events;

import fr.pacifista.bot.discord.modules.core.events.buttons.ButtonEvent;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.List;

public class TicketCreateButton implements ButtonEvent {
    List<SelectOption> getOptions() {
        List<SelectOption> options = new ArrayList<>();

        SelectOption onlinePurchase = SelectOption
                .of("Achat en ligne", "online_purchase")
                .withDescription("Un problème avec un achat en ligne ?")
                .withEmoji(Emoji.fromUnicode("🛒"));

        SelectOption reclamation = SelectOption
                .of("Réclamation", "reclamation")
                .withDescription("Réclamer un achat")
                .withEmoji(Emoji.fromUnicode("🎁"));

        SelectOption report = SelectOption
                .of("Signalement", "report")
                .withDescription("Signaler un joueur")
                .withEmoji(Emoji.fromUnicode("🚩"));

        SelectOption bug = SelectOption
                .of("Bug", "bug")
                .withDescription("Signaler un bug")
                .withEmoji(Emoji.fromUnicode("🐛"));

        SelectOption other = SelectOption
                .of("Autre", "other")
                .withDescription("Contacter l'équipe de Pacifista")
                .withEmoji(Emoji.fromUnicode("☎️"));

        options.add(onlinePurchase);
        options.add(reclamation);
        options.add(report);
        options.add(bug);
        options.add(other);

        return options;
    }

    @Override
    public void onButton(@NonNull ButtonInteractionEvent event) {
        SelectMenu select = StringSelectMenu.create("create-ticket")
                .setPlaceholder("Sélectionne le type de ticket")
                .setId("ticket-create")
                .setRequiredRange(1, 1)
                .addOptions(getOptions())
                .build();

        event.reply("Merci de choisir un type de ticket ci-dessous.")
                .setEphemeral(true)
                .setActionRow(select)
                .queue();
    }
}
