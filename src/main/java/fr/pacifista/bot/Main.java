package fr.pacifista.bot;

import fr.pacifista.bot.modules.ScheduledTasks;
import fr.pacifista.bot.modules.ConsoleCommands;
import fr.pacifista.bot.utils.BotException;
import fr.pacifista.bot.utils.ConsoleColors;

import java.io.File;

public class Main {
    public static final File dataFolder = new File("data");
    public static volatile Main instance = null;

    private Bot bot;

    Main() throws BotException {
        instance = this;
        bot = new Bot();
        ConsoleCommands.setupConsole();
        ScheduledTasks.init();
    }

    public void reload() throws BotException {
        System.out.println(ConsoleColors.YELLOW + "Rechargement du bot..." + ConsoleColors.RESET);
        bot = new Bot();
        System.out.println(ConsoleColors.GREEN + "Rechargement terminé !" + ConsoleColors.RESET);
    }

    public void stop() {
        System.out.println("Arrêt du bot");
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            new Main();
        } catch (BotException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
