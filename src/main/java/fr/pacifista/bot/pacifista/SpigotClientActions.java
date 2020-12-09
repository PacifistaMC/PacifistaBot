package fr.pacifista.bot.pacifista;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import fr.pacifista.bot.Bot;
import fr.pacifista.bot.utils.BotException;

import java.util.UUID;

import static fr.pacifista.bot.pacifista.SocketDiscordClientCode.*;

public class SpigotClientActions {

    protected static void onReceivedMessage(final String response) {
        try {
            JsonObject res = JsonParser.parseString(response).getAsJsonObject();

            switch (res.get("type").getAsString()) {
                case FETCH_PLAYER_DATA:
                    fetchPlayersPacifista(res);
                    break;
                case Events.PLAYER_CHAT:
                    final String playerName = res.get("name").getAsString();
                    final String message = res.get("message").getAsString();

                    Bot.sendMessageToChannel("**" + playerName + "**" + " » " + message + "", Bot.getConfiguration().pacifistaChatID);
                    break;
                case Events.PLAYER_JOIN_LEAVE_EVENT:
                    final boolean isConnecting = res.get("isConnecting").getAsBoolean();
                    final UUID playerUUID = UUID.fromString(res.get("uuid").getAsString());
                    final String username = res.get("name").getAsString();

                    Bot.sendMessageToChannel("> **" + username + "** " + (isConnecting ? "s'est connecté sur Pacifista" : "s'est déconnecté de Pacifista"), Bot.getConfiguration().pacifistaChatID);
                    break;
            }
        } catch (IllegalStateException | JsonSyntaxException | NullPointerException ignored) {
        } catch (BotException e) {
            e.printStackTrace();
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
