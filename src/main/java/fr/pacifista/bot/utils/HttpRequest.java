package fr.pacifista.bot.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class HttpRequest {

    private final URL url;
    private final String method;

    public HttpRequest(final String url, final String method) throws BotException {
        try {
            this.url = new URL(url);
            this.method = method;
        } catch (MalformedURLException e) {
            throw new BotException(e.getMessage());
        }
    }

    public JsonObject doJsonRequest() throws BotException {
        return JsonParser.parseString(doRequest()).getAsJsonObject();
    }

    private String doRequest() throws BotException {
        InputStreamReader reader = null;
        HttpURLConnection http = null;

        try {
            URLConnection urlConnection = url.openConnection();
            http = (HttpURLConnection) urlConnection;
            http.setRequestMethod(method);

            if (http.getResponseCode() != 200)
                throw new BotException(BotException.ERROR_HTTP_NOT_200);
            reader = new InputStreamReader(http.getInputStream());
            return JsonParser.parseReader(reader).toString();
        } catch (IOException e) {
            throw new BotException(e.getMessage());
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (http != null)
                    http.disconnect();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}
