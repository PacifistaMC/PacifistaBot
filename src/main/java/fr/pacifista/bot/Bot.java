package fr.pacifista.bot;

import fr.pacifista.bot.Events.UserJoinLeave;
import fr.pacifista.bot.Events.UserMessage;
import fr.pacifista.bot.Modules.BotConfiguration;
import fr.pacifista.bot.Utils.ConsoleColors;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.NoSuchElementException;

public class Bot {

    private JDA api;
    private final BotConfiguration botConfiguration;

    private Bot(BotConfiguration botConfiguration) throws LoginException, InterruptedException {
        this.botConfiguration = botConfiguration;
        setupBot();
    }

    private void setupBot() throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(botConfiguration.discordToken);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setActivity(Activity.of(Activity.ActivityType.WATCHING, "twitch.tv/funixgaming", "https://twitch.tv/funixgaming"));
        builder.addEventListeners(new UserMessage(), new UserJoinLeave());
        this.api = builder.build().awaitReady();
    }

    public JDA getApi() { return this.api; }
    public BotConfiguration getConfig() { return this.botConfiguration; }

    public static Bot initBot() {
        Bot bot;
        try {
            BotConfiguration botConfig = BotConfiguration.getConfiguration();
            bot = new Bot(botConfig);
            return bot;
        } catch (IOException | LoginException | NoSuchElementException | InterruptedException e) {
            System.err.println("Une erreur est survenue lors de la connection du fr.pacifista.bot. Veuillez recommencer.");
            System.err.println(ConsoleColors.RED + e.getMessage());
            System.exit(84);
            BotConfiguration.removeConfigFile();
            return null;
        }
    }
}
