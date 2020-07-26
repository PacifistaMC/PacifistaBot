package fr.pacifista.bot.Modules;

import fr.pacifista.bot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class BotActions {

    public static void sendMessageToChannel(String message, String channelID) {
        Guild guild = Main.instance.getBot().getApi().getGuilds().get(0);
        TextChannel channel = guild.getTextChannelById(channelID);
        if (channel == null)
            return;
        channel.sendMessage(message).queue();
    }

    public static void sendMessageToChannel(MessageEmbed message, String channelID) {
        Guild guild = Main.instance.getBot().getApi().getGuilds().get(0);
        TextChannel channel = guild.getTextChannelById(channelID);
        if (channel == null)
            return;
        channel.sendMessage(message).queue();
    }
}
