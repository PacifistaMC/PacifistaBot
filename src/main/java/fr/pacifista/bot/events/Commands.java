package fr.pacifista.bot.events;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.Main;
import fr.pacifista.bot.modules.ConsoleCommands;
import fr.pacifista.bot.utils.BotException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.List;

public class Commands {

    public static void ip(User user, TextChannel channel, List<String> args) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Pacifista Minecraft", "https://pacifista.fr");
        embedBuilder.setDescription("Serveur minecraft survie");
        embedBuilder.addField("Site web", "https://pacifista.fr", true);
        embedBuilder.addField("IP de connexion", "play.pacifista.fr", true);
        embedBuilder.addField("Version", "1.16.3", true);
        embedBuilder.setColor(new Color(0, 168, 232));

        channel.sendMessage(embedBuilder.build()).queue();
    }

    public static void logs(User user, TextChannel channel, List<String> args) {
        try {
            if (!channel.getId().equals(Bot.getConfiguration().logID)) return;
            ConsoleCommands.getLogs(args);
        } catch (BotException e) {
            e.printStackTrace();
        }
    }

}
