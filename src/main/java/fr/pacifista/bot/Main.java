package fr.pacifista.bot;

import fr.pacifista.bot.Modules.ScheduledTasks;
import fr.pacifista.bot.Utils.ConsoleCommands;

import java.io.File;

public class Main {
    public static final File dataFolder = new File("data");

    public static Bot bot;

    public static void main(String[] args) {
        bot = Bot.initBot();
        ConsoleCommands.setupConsole();
        //ScheduledTasks.init();
    }
}
