package fr.pacifista.bot;

import fr.pacifista.bot.events.UserJoinLeave;
import fr.pacifista.bot.events.UserMessage;
import fr.pacifista.bot.utils.BotException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Bot {

    private static volatile Bot instance = null;

    private final JDA api;
    protected final BotConfiguration botConfiguration;

    protected Bot() throws BotException {
        instance = null;

        try {
            this.botConfiguration = BotConfiguration.getConfiguration();
            JDABuilder builder = JDABuilder.createDefault(this.botConfiguration.discordToken);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.addEventListeners(new UserMessage(), new UserJoinLeave(this, botConfiguration));
            this.api = builder.build().awaitReady();
        } catch (LoginException | InterruptedException | IOException e) {
            throw new BotException(e.getMessage());
        } finally {
            instance = this;
            refreshActivityMsg(0);
        }
    }

    private static Bot getInstance() throws BotException {
        if (instance == null)
            throw new BotException(BotException.BOT_SESSION_NOT_EXISTS);
        return instance;
    }

    public static void refreshActivityMsg(int players) {
        if (instance == null)
            return;

        Activity activity;

        if (players < 0) {
            activity = Activity.of(Activity.ActivityType.WATCHING, "Serveur hors ligne", "https://pacifista.fr");
        } else {
            activity = Activity.of(Activity.ActivityType.WATCHING, players + " joueurs", "https://pacifista.fr");
        }
        instance.api.getPresence().setActivity(activity);
    }

    public static BotConfiguration getConfiguration() throws BotException {
        final Bot bot = getInstance();

        return bot.botConfiguration;
    }

    public static Guild getPacifistaGuild() throws BotException {
        final Bot bot = getInstance();

        for (Guild g : bot.api.getGuilds()) {
            if (g.getId().equals(bot.botConfiguration.pacifistaGuildID))
                return g;
        }

        throw new BotException(BotException.BOT_SESSION_NOT_EXISTS);
    }

    public static void sendMessageToChannel(final String message, String channelID) throws BotException {
        Guild guild = getPacifistaGuild();
        TextChannel channel = guild.getTextChannelById(channelID);
        if (channel == null)
            return;
        channel.sendMessage(message).queue();
    }

    public static void sendMessageToChannel(final MessageEmbed message, final String channelID) throws BotException {
        Guild guild = getPacifistaGuild();
        TextChannel channel = guild.getTextChannelById(channelID);
        if (channel == null)
            return;
        channel.sendMessage(message).queue();
    }

    public static TextChannel getChannelByID(final String channelID) throws BotException {
        Bot bot = getInstance();

        return bot.api.getTextChannelById(channelID);
    }
}
