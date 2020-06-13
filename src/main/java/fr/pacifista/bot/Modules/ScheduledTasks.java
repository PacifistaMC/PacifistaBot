package fr.pacifista.bot.Modules;

import fr.pacifista.bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduledTasks {

    public static void init() {
        new Thread(() -> {
            Timer timer = new Timer();

            new NotificationLive(timer, 10000);
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

class NotificationLive extends Task {

    private final String twitchChannelID;
    private final String botUrlProfileImage;
    private boolean isStreaming;

    NotificationLive(Timer timer, int period) {
        super(timer, period);
        this.isStreaming = false;
        this.twitchChannelID = Main.bot.getConfig().twitchID;
        this.botUrlProfileImage = Main.bot.getApi().getSelfUser().getAvatarUrl();
    }

    void task() {
        /*try {
            Main.twitchApi.fetchStream();
            TwitchApi twitchApi = Main.twitchApi;
            boolean apiStream = twitchApi.isLive;
            if (!isStreaming && apiStream) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("FunixGaming est en live !", "https://twitch.tv/funixgaming");
                embedBuilder.addField("Titre du live :", twitchApi.streamTitle, false);
                embedBuilder.addField("Jeu :", twitchApi.gameName, false);
                embedBuilder.addField("Lien :", "https://twitch.tv/funixgaming", false);
                embedBuilder.setThumbnail(twitchApi.gameJacket);
                embedBuilder.setColor(new Color(100, 65, 165));
                embedBuilder.setFooter("FunixLive - Notification de live", botUrlProfileImage);
                BotActions.sendMessageToChannel("FunixGaming est en live sur Twitch ! @here", twitchChannelID);
                BotActions.sendMessageToChannel(embedBuilder.build(), twitchChannelID);
            } else if (isStreaming && !apiStream) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Fin du live.");
                embedBuilder.setDescription("Merci d'avoir suivi le live !");
                embedBuilder.addField("Titre du live :", twitchApi.streamTitle, false);
                embedBuilder.setColor(new Color(100, 65, 165));
                embedBuilder.setFooter("FunixLive - Notification de live", botUrlProfileImage);
                BotActions.sendMessageToChannel(embedBuilder.build(), twitchChannelID);
            }
            this.isStreaming = apiStream;
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
