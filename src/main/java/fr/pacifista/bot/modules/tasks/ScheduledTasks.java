package fr.pacifista.bot.modules.tasks;

import java.util.Timer;

public class ScheduledTasks {

    public static void init() {
        new Thread(() -> {
            Timer timer = new Timer();

            new RefreshBotActivity(timer, 10000);
        }).start();
    }
}
