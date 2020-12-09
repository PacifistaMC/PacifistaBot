package fr.pacifista.bot.pacifista;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import fr.pacifista.bot.Bot;

import static fr.pacifista.bot.pacifista.SocketDiscordClientCode.*;

public class SpigotClientActions {

    protected static void onReceivedMessage(final String response) {
        try {
            JsonObject res = JsonParser.parseString(response).getAsJsonObject();

            if (res.get("type").getAsString().equals(FETCH_PLAYER_DATA))
                fetchPlayersPacifista(res);
        } catch (IllegalStateException | JsonSyntaxException ignored) {
        }
    }

    private static void fetchPlayersPacifista(final JsonObject data) {
        final int playerCount = data.get("playerCount").getAsInt();
        /*final JsonArray playersData = data.get("data").getAsJsonArray();
        final int arraySize = playersData.size();*/

        Bot.refreshActivityMsg(playerCount);
        /*for (int i = 0; i < arraySize; ++i) {
            final JsonObject playerData = playersData.get(i).getAsJsonObject();
            final String playerName = playerData.get("playerName").getAsString();
            final UUID playerUUID = UUID.fromString(playerData.get("uuid").getAsString());
            final String serverName = playerData.get("server").getAsString();
            final int playerPing = playerData.get("ping").getAsInt();
        }*/
    }

}
