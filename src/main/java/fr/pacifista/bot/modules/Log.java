package fr.pacifista.bot.modules;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.utils.BotException;
import fr.pacifista.bot.utils.FileActions;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private static final File logFolder = new File("data", "logs");

    public static void logMessage(User user, TextChannel textChannel, String message) {
        try {
            if (textChannel.getId().equals(Bot.getConfiguration().pacifistaChatID))
                return;
        } catch (BotException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            String dateFormatted = dateFormat.format(date);
            String time = timeFormat.format(date);

            if (!logFolder.exists() && !logFolder.mkdirs()) {
                System.err.println("Error while creating data and logs folder.");
                return;
            }

            try {
                File logFile = new File(logFolder, dateFormatted + ".log");
                if (!logFile.exists() && !logFile.createNewFile())
                    throw new IOException("Could not create file");
                String log = time + " > " + user.getAsTag() + " [" + textChannel.getName() + "] " + message + "\n";
                System.out.print(log);
                FileActions.writeInFile(logFile, log, true);
            } catch (IOException e) {
                BotException botException = new BotException(e.getMessage());
                botException.printStackTrace();
            }
        }).start();
    }

}
