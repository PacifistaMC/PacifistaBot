package fr.pacifista.bot.discord.commands;

import fr.pacifista.bot.discord.modules.core.commands.BotCommand;
import fr.pacifista.bot.discord.modules.core.utils.Colors;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandIP extends BotCommand {

    private static final String WEBSITE_URL = "https://pacifista.fr";

    private final MessageEmbed messageEmbed = new EmbedBuilder()
            .setTitle("Pacifista Minecraft", WEBSITE_URL)
            .setDescription("Serveur minecraft survie")
            .addField("Site web", WEBSITE_URL, true)
            .addField("IP de connexion", "play.pacifista.fr", true)
            .addField("Version", "1.20.6", true)
            .setColor(Colors.PACIFISTA_COLOR)
            .build();

    private final List<ItemComponent> buttons = List.of(
            Button.link(
                    WEBSITE_URL,
                    "Accéder au site Web"
            ).withEmoji(Emoji.fromUnicode("🌐")),
            Button.link(
                    WEBSITE_URL + "/join",
                    "Se connecter au serveur"
            ).withEmoji(Emoji.fromUnicode("🎮")),
            Button.link(
                    WEBSITE_URL + "/shop",
                    "Accéder à la boutique"
            ).withEmoji(Emoji.fromUnicode("🛒"))
    );

    public CommandIP(JDA jda) {
        super(jda, null);
    }

    @Override
    public String getCommandName() {
        return "ip";
    }

    @Override
    public String getCommandDescription() {
        return "Affiche l'ip du serveur";
    }

    @Override
    public DefaultMemberPermissions getCommandPermissions() {
        return DefaultMemberPermissions.ENABLED;
    }

    @Override
    public void onCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        interactionEvent.replyEmbeds(messageEmbed).addActionRow(buttons).queue();
    }

}
