package fr.pacifista.bot.modules.tasks;

import com.google.gson.JsonObject;
import fr.pacifista.bot.pacifista.SocketClientSpigot;
import fr.pacifista.bot.pacifista.SocketDiscordClientCode;
import fr.pacifista.bot.utils.BotException;

import java.util.Timer;

public class RefreshBotActivity extends BotTask {
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
