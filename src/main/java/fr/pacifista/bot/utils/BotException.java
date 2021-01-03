package fr.pacifista.bot.utils;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotException extends Exception {

    public static final String BOT_SESSION_NOT_EXISTS = "Le bot n'est pas initialisé. Veuillez patienter que le bot soit configuré et réessayez.";
    public static final String PACIFISTA_SOCKET_NOT_CONNECTED = "Le bot n'est pas connecté à Pacifista.";
    public static final String ERROR_HTTP_NOT_200 = "Le serveur http n'a pas retourné un code 200";

    public BotException(final String message) {
        super(message);
    }

    public String getPublicErrorMessage() {
        return ":warning: ``Une erreur est survenue, veuillez réessayer plus tard ou contacter un staff.``";
    }

    public void printErrorMessage() {
        System.err.println(ConsoleColors.RED + "[BotException] -> " + this.getMessage() + ConsoleColors.RESET);
    }

    @Override
    public void printStackTrace() {
        new Thread(() -> {
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

                try {
                    final EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.RED);
                    embedBuilder.setTitle("Erreur du bot");
                    embedBuilder.setFooter("PacifistaBot - Erreur report");
                    embedBuilder.addField("Fichier de log erreur", logFile.getName(), true);
                    embedBuilder.addField("Message d'erreur", this.getMessage(), true);
                    embedBuilder.addField("Stack principale", this.getStackTrace()[0].toString(), false);
                    Bot.sendMessageToChannel(embedBuilder.build(), Bot.getConfiguration().logChannelID);
                } catch (BotException e) {
                    System.err.println(ConsoleColors.RED + "Impossible d'envoyer le embed d'erreur dans log." + ConsoleColors.RESET);
                }

                for (StackTraceElement trace : this.getStackTrace()) {
                    FileActions.writeInFile(logFile, trace.toString() + "\n", true);
                }
                FileActions.writeInFile(logFile, "\n", true);
                System.err.println(ConsoleColors.RED + "[BotException] -> " + this.getMessage() + " (errorLogs/" + logFile.getName() + ")" + ConsoleColors.RESET);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
