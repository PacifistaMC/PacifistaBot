package fr.pacifista.bot.utils;

import fr.pacifista.bot.Main;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class BotException extends Exception {

    public static final String BOT_SESSION_NOT_EXISTS = "Le bot n'est pas initialisé. Veuillez patienter que le bot soit configuré et réessayez.";

    public BotException(final String message) {
        super(message);
    }

    public String getPublicErrorMessage() {
        return "Une erreur est survenue, veuillez réessayer plus tard ou contacter un staff.";
    }

    @Override
    public void printStackTrace() {
        try {
            final DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
            final Date date = new Date();
            final String dateFormatted = dateFormat.format(date);
            final File folderErrorLog = new File(Main.dataFolder, "errorLogs");
            final File logFile = new File(folderErrorLog, dateFormatted + ".log");

            if (!folderErrorLog.exists() && !folderErrorLog.mkdir())
                throw new IOException("Could not create folder " + folderErrorLog.getName());
            if (!logFile.exists() && !logFile.createNewFile())
                throw new IOException("Could not create file " + logFile.getName());
            FileActions.writeInFile(logFile, Arrays.toString(this.getStackTrace()) + "\n", true);
            System.err.println(ConsoleColors.RED + "[BotException] -> " + this.getMessage() + " (errorLogs/" + logFile.getName() + ")" + ConsoleColors.RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
