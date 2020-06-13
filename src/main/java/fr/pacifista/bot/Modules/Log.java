package fr.pacifista.bot.Modules;

import fr.pacifista.bot.Utils.FileActions;
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
        new Thread(() -> {
            DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            String dateFormated = dateFormat.format(date);
            String time = timeFormat.format(date);

            if (!logFolder.exists() && !logFolder.mkdirs()) {
                System.err.println("Error while creating data and logs folder.");
                return;
            }

            try {
                File logFile = new File(logFolder, dateFormated + ".log");
                if (!logFile.exists() && !logFile.createNewFile())
                    throw new IOException("Could not create file");
                String log = time + " > " + user.getAsTag() + " [" + textChannel.getName() + "] " + message + "\n";
                System.out.print(log);
                FileActions.writeInFile(logFile, log, true);
            } catch (IOException e) {
                System.err.println("Error on today log file.");
                e.printStackTrace();
            }
        }).start();
    }

}
