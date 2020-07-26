package fr.pacifista.bot.minecraftLink;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.pacifista.bot.Modules.BotConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class PacifistaInfos {

    private int playerCount = -1;
    private int maxPlayers = -1;

    PacifistaInfos(BotConfiguration botConfiguration) {
        InputStreamReader reader = null;
        HttpURLConnection http = null;

        try {
            URL url = new URL("http://" + botConfiguration.pacifistaAddress + ":" + botConfiguration.pacifistaPort + "/api/serverinfo");
            URLConnection urlConnection = url.openConnection();
            http = (HttpURLConnection) urlConnection;
            http.setRequestMethod("GET");

            if (http.getResponseCode() != 200)
                throw new IOException("Serveur error. CODE: " + http.getResponseCode());
            reader = new InputStreamReader(http.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            this.playerCount = json.get("connectedPlayers").getAsInt();
            this.maxPlayers = json.get("maxPlayers").getAsInt();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (http != null)
                    http.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
