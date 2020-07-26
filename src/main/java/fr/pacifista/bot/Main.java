package fr.pacifista.bot;

import fr.pacifista.bot.Modules.ScheduledTasks;
import fr.pacifista.bot.Modules.ConsoleCommands;
import fr.pacifista.bot.Utils.ConsoleColors;
import fr.pacifista.bot.minecraftLink.BotSocket;

import java.io.File;

public class Main {
    public static final File dataFolder = new File("data");

    public static Main instance;

    private final Bot bot;
    private BotSocket botSocketThread;

    Main() {
        instance = this;
        bot = Bot.initBot();
        ConsoleCommands.setupConsole();
        ScheduledTasks.init();
        botSocketThread = new BotSocket(bot);
        botSocketThread.start();
    }

    public void reload() {
        System.out.println(ConsoleColors.YELLOW + "Reload du bot en cours...");
        bot.reloadConfig();
        botSocketThread.stopSocket();
        botSocketThread = new BotSocket(bot);
        botSocketThread.start();
        System.out.println(ConsoleColors.GREEN + "Reload terminé !");
    }

    public void stop() {
        System.out.println("Arrêt du bot");
        if (botSocketThread != null)
            botSocketThread.stopSocket();
        System.exit(0);
    }

    public Bot getBot() {
        return bot;
    }

    public static void main(String[] args) {
        new Main();
    }
}
