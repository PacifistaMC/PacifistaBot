package fr.pacifista.bot.Modules;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.pacifista.bot.Utils.ConsoleColors;
import fr.pacifista.bot.Utils.FileActions;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import static fr.pacifista.bot.Main.dataFolder;

public class TwitchApi {
    private final ApiConfiguration apiConfiguration;
    private BearerToken bearerToken;

    public boolean isLive = false;
    public String streamTitle = null;
    public String gameName = null;
    public String gameJacket = null;
    public int nbViewers = 0;

    private TwitchApi() throws IOException {
        this.apiConfiguration = ApiConfiguration.getConfig();
        this.bearerToken = BearerToken.getToken(this.apiConfiguration);
    }

    public void fetchStream() throws IOException {
        Gson gson = new Gson();
        this.bearerToken = BearerToken.getToken(this.apiConfiguration);

        URL url = new URL("https://api.twitch.tv/helix/streams?user_login=" + apiConfiguration.channelName);
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) urlConnection;
        http.setRequestMethod("GET");
        http.setRequestProperty("Client-ID", apiConfiguration.clientID);
        http.setRequestProperty("Authorization", "Bearer " + bearerToken.accessToken);
        if (http.getResponseCode() != 200)
            return;

        InputStreamReader reader = new InputStreamReader(http.getInputStream());
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        JsonArray jsonArray = json.get("data").getAsJsonArray();
        if (jsonArray.size() < 1) {
            this.isLive = false;
            this.gameJacket = null;
            this.gameName = null;
            this.nbViewers = 0;
            this.streamTitle = null;
            return;
        }
        JsonObject streamData = jsonArray.get(0).getAsJsonObject();
        this.isLive = true;
        this.nbViewers = streamData.get("viewer_count").getAsInt();
        this.streamTitle = streamData.get("title").getAsString();
        getGame(streamData.get("game_id").getAsString());
        reader.close();
    }

    private void getGame(String gameID) throws IOException {
        Gson gson = new Gson();
        this.bearerToken = BearerToken.getToken(this.apiConfiguration);

        URL url = new URL("https://api.twitch.tv/helix/games?id=" + gameID);
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) urlConnection;
        http.setRequestMethod("GET");
        http.setRequestProperty("Client-ID", apiConfiguration.clientID);
        http.setRequestProperty("Authorization", "Bearer " + bearerToken.accessToken);
        if (http.getResponseCode() != 200)
            return;

        InputStreamReader reader = new InputStreamReader(http.getInputStream());
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        JsonObject gameData = json.get("data").getAsJsonArray().get(0).getAsJsonObject();
        this.gameName = gameData.get("name").getAsString();
        this.gameJacket = gameData.get("box_art_url").getAsString().replace("{width}", "600").replace("{height}", "840");
        reader.close();
    }

    public static TwitchApi init() {
        try {
            System.out.println(ConsoleColors.PURPLE + "Initialisation du module TwitchAPI..." + ConsoleColors.WHITE);
            TwitchApi twitchApi = new TwitchApi();
            twitchApi.fetchStream();
            System.out.println(ConsoleColors.GREEN + "Initialisation terminée." + ConsoleColors.WHITE);
            return twitchApi;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(84);
            return null;
        }
    }

}

class ApiConfiguration {
    private static final File configFile = new File(dataFolder, "twitchApiConfiguration.json");

    public String channelName;
    public String clientID;
    public String clientSecret;

    private ApiConfiguration(boolean generate) {
        if (!generate) return;
        Scanner scanner = new Scanner(System.in);
        System.out.println(ConsoleColors.PURPLE + "Pseudo twitch pour les checks api:");
        channelName = scanner.nextLine();
        System.out.println(ConsoleColors.PURPLE + "Client_id:");
        clientID = scanner.nextLine();
        System.out.println(ConsoleColors.PURPLE + "Client_secret:");
        clientSecret = scanner.nextLine();
    }

    public static ApiConfiguration getConfig() throws IOException {
        String objString;
        Gson gson = new Gson();

        if (!dataFolder.exists() && !dataFolder.mkdir())
            throw new IOException("Could not create data folder.");
        if (!configFile.exists()) {
            if (!configFile.createNewFile())
                throw new IOException("Could not create twitch api config file.");
            ApiConfiguration config = new ApiConfiguration(true);
            objString = gson.toJson(config);
            FileActions.writeInFile(configFile, objString, false);
            return config;
        } else {
            objString = FileActions.getFileContent(configFile);
            return gson.fromJson(objString, ApiConfiguration.class);
        }
    }
}

class BearerToken {
    private static final File bearerFile = new File(dataFolder, "twitchApiBearer.json");

    private final long generatedTimestamp;
    private final int expiresIn;
    public final String accessToken;

    private BearerToken(long generatedTimestamp, int expiresIn, String accessToken) {
        this.generatedTimestamp = generatedTimestamp;
        this.expiresIn = expiresIn;
        this.accessToken = accessToken;
    }

    public boolean isTokenExpired() {
        long diff = System.currentTimeMillis() / 1000 - generatedTimestamp / 1000;
        return diff >= expiresIn - 60;
    }

    public static BearerToken getToken(ApiConfiguration configuration) throws IOException {
        BearerToken bearerToken;
        Gson gson = new Gson();

        if (!dataFolder.exists() && !dataFolder.mkdir())
            throw new IOException("Could not create data folder.");
        if (!bearerFile.exists()) {
            if (!bearerFile.createNewFile())
                throw new IOException("Could not create twitch bearer file token.");
            bearerToken = generateNewToken(configuration);
            FileActions.writeInFile(bearerFile, gson.toJson(bearerToken), false);
            return bearerToken;
        }
        bearerToken = gson.fromJson(FileActions.getFileContent(bearerFile), BearerToken.class);
        if (bearerToken.isTokenExpired()) {
            bearerToken = generateNewToken(configuration);
            FileActions.writeInFile(bearerFile, gson.toJson(bearerToken), false);
        }
        return bearerToken;
    }

    private static BearerToken generateNewToken(ApiConfiguration configuration) throws IOException {
        Gson gson = new Gson();

        URL url = new URL("https://id.twitch.tv/oauth2/token?client_id=" + configuration.clientID +
                "&client_secret=" + configuration.clientSecret + "&grant_type=client_credentials");
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) urlConnection;
        http.setRequestMethod("POST");
        if (http.getResponseCode() != 200)
            throw new IOException("Error when getting bearrer token ERROR: " + http.getResponseCode());

        InputStreamReader reader = new InputStreamReader(http.getInputStream());
        JsonObject json = gson.fromJson(reader, JsonObject.class);
        BearerToken bearerToken = new BearerToken(
                System.currentTimeMillis(),
                json.get("expires_in").getAsInt(),
                json.get("access_token").getAsString()
        );
        reader.close();
        System.out.println(ConsoleColors.YELLOW + "Génération d'un nouveau token bearer." + ConsoleColors.WHITE);
        return bearerToken;
    }
}
