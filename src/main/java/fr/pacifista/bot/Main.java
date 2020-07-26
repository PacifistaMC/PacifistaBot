package fr.pacifista.bot;

import fr.pacifista.bot.Modules.ScheduledTasks;
import fr.pacifista.bot.Modules.ConsoleCommands;
import fr.pacifista.bot.Utils.ConsoleColors;

import java.io.File;

public class Main {
    public static final File dataFolder = new File("data");

    public static Main instance;

    private final Bot bot;

    Main() {
        instance = this;
        bot = Bot.initBot();
        ConsoleCommands.setupConsole();
        ScheduledTasks.init();
    }

    public void reload() {
        System.out.println(ConsoleColors.YELLOW + "Reload du bot en cours...");
        bot.reloadConfig();
        System.out.println(ConsoleColors.GREEN + "Reload terminé !");
    }

    public Bot getBot() {
        return bot;
    }

    public static void main(String[] args) {
        new Main();
    }
}
