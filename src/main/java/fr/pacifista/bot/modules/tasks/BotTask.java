package fr.pacifista.bot.modules.tasks;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BotTask {
    BotTask(Timer timer, int period) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task();
            }
        }, 0, period);
    }

    abstract void task();


}
