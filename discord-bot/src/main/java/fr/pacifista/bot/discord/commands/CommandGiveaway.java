package fr.pacifista.bot.discord.commands;

import fr.pacifista.bot.core.GiveawaysManager;
import fr.pacifista.bot.core.entities.giveaways.Giveaway;
import fr.pacifista.bot.discord.PacifistaBot;
import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandGiveaway extends Command {
    private final GiveawaysManager giveawaysManager;

    protected CommandGiveaway(PacifistaBot pacifistaBot, GiveawaysManager giveawaysManager) {
        super(pacifistaBot.getJda(), List.of(
                new SubcommandData("start", "Commencer un giveaway !"),
                new SubcommandData("roll", "Choisir les gagnants du giveaway !")
        ));
        this.giveawaysManager = giveawaysManager;
    }

    @Override
    public String getCommandName() {
        return "giveaway";
    }

    @Override
    public String getCommandDescription() {
        return "Permet de créer et terminer un giveaway !";
    }

    @Override
    public DefaultMemberPermissions getCommandPermissions() {
        return DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL);
    }

    @Override
    public void onCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        switch (interactionEvent.getSubcommandName()) {
            case "start":
                startGiveaway(interactionEvent);
                break;
            case "roll":
                rollGiveaway(interactionEvent);
                break;
        }
    }

    private void startGiveaway(@NonNull SlashCommandInteractionEvent interactionEvent) {
        TextInput prize = TextInput.create("giveaway-prize", "Récompense du giveaway", TextInputStyle.SHORT)
                .setPlaceholder("Exemple: 1 mois de Pacifista+")
                .setMinLength(1)
                .setMaxLength(50)
                .setRequired(true)
                .build();

        TextInput pacifistaCommandToSend = TextInput.create(
                    "giveaway-pacifista-command",
                    "Commande à envoyer au serveur",
                    TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(100)
                .setRequired(true)
                .build();

        TextInput winners = TextInput.create("giveaway-winners", "Nombre de gagnants", TextInputStyle.SHORT)
                .setPlaceholder("1")
                .setMinLength(1)
                .setMaxLength(2)
                .setRequired(false)
                .build();

        Modal modal = Modal.create("giveaway-create", "Crée un giveaway")
                .addActionRow(prize)
                .addActionRow(pacifistaCommandToSend)
                .addActionRow(winners)
                .build();

        interactionEvent.replyModal(modal).queue();
    }

    private void rollGiveaway(@NonNull SlashCommandInteractionEvent interactionEvent) {
        List<Giveaway> giveawayList = this.giveawaysManager.getGiveaways();
        if (giveawayList.isEmpty()) {
            interactionEvent.reply("Aucun giveaway n'est actuellement en cours.")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("giveaway-roll");

        for (Giveaway gw : giveawayList) {
            menuBuilder.addOption(
                    gw.getPrize(),
                    gw.getGiveawayId().toString(),
                    String.format("ID: %s", gw.getGiveawayId())
            );
        }

        interactionEvent.reply("Quel giveaway doit être terminé ?")
                .addActionRow(menuBuilder.build())
                .setEphemeral(true)
                .queue();
    }
}
