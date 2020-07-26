package fr.pacifista.bot.Modules;

import fr.pacifista.bot.Main;

import java.util.Timer;
import java.util.TimerTask;

public class ScheduledTasks {

    public static void init() {
        new Thread(() -> {
            Timer timer = new Timer();

            new RefreshBotActivity(timer, 10000);
        }).start();
    }
}

abstract class Task {
    Task(Timer timer, int period) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task();
            }
        }, 0, period);
    }

    abstract void task();
}

class RefreshBotActivity extends Task {

    RefreshBotActivity(Timer timer, int period) {
        super(timer, period);
    }

    @Override
    void task() {
        Main.instance.getBot().refreshActivityMsg();
    }
}
