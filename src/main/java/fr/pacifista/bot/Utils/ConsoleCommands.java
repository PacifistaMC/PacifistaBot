package fr.pacifista.bot.Utils;

import fr.pacifista.bot.Main;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleCommands {

    private static boolean RUNNING = true;

    public static void setupConsole() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (RUNNING) {
                final String userEntry = scanner.nextLine();

                List<String> args = new ArrayList<>();
                Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(userEntry);
                while (m.find())
                    args.add(m.group(1));
                if (args.size() < 1) continue;
                String command = args.get(0);
                args.remove(0);

                try {
                    Method[] commandList = ConsoleCommands.class.getDeclaredMethods();
                    for (Method method : commandList) {
                        String methodName = method.getName();
                        if (methodName.equals("setupConsole")) continue;
                        if (methodName.equalsIgnoreCase(command)) {
                            method.invoke(ConsoleCommands.class, args);
                            break;
                        }
                    }
                } catch (InvocationTargetException | IllegalAccessException noSuchMethodException) {
                    noSuchMethodException.printStackTrace();
                }
            }
        }).start();
    }

    public static void stop(List<String> args) {
        RUNNING = false;
        System.out.println("Arrêt du fr.pacifista.bot.");
        System.exit(0);
    }

    public static void getLogs(List<String> args) {
        String dateFormated;
        if (args.size() < 1) {
            DateFormat dateFormat = new SimpleDateFormat("d-MM-yyyy");
            Date date = new Date();
            dateFormated = dateFormat.format(date);
        } else
            dateFormated = args.get(0);
        System.out.println("Envoi des logs : " + dateFormated + ".log");

        TextChannel logChannel = Main.bot.getApi().getTextChannelById(Main.bot.getConfig().logID);
        if (logChannel == null)
            return;
        File logFile = new File(Main.dataFolder, "logs/" + dateFormated + ".log");
        if (logFile.exists()) {
            logChannel.sendFile(logFile, logFile.getName()).queue();
        } else {
            String err = "Le fichier n'existe pas. ``" + logFile.getName() + "``";
            logChannel.sendMessage(err).queue();
            System.out.println(err);
        }
    }
}
