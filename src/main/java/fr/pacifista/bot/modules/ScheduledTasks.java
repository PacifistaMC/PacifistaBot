package fr.pacifista.bot.modules;

import com.google.gson.JsonObject;
import fr.pacifista.bot.pacifista.SocketClientSpigot;
import fr.pacifista.bot.pacifista.SocketDiscordClientCode;
import fr.pacifista.bot.utils.BotException;

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
        try {
            final JsonObject toSend = new JsonObject();

            toSend.addProperty("code", SocketDiscordClientCode.FETCH_PLAYER_DATA);
            SocketClientSpigot.sendMessageToServer(toSend.toString());
        } catch (BotException e) {
            e.printErrorMessage();
        }
    }
}
