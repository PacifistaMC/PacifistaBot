package fr.pacifista.bot;

import com.google.gson.Gson;
import fr.pacifista.bot.utils.ConsoleColors;
import fr.pacifista.bot.utils.FileActions;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static fr.pacifista.bot.Main.dataFolder;

public class BotConfiguration {

    private static final File configFile = new File(dataFolder, "botConfiguration.json");

    public String discordToken;
    public String bienvenueID;
    public String logID;
    public String playerID;
    public String adminID;
    public String pacifistaChatID;
    public String pacifistaGuildID;

    public String pacifistaAddress;
    public int pacifistaPort;

    private void checkConfig() throws IOException {
        final Scanner scanner = new Scanner(System.in);

        if (this.discordToken == null) {
            System.out.print(ConsoleColors.GREEN + "Veuillez entrer le token du bot discord:" + System.lineSeparator() + ConsoleColors.RESET);
            this.discordToken = scanner.nextLine();;
        }
        if (this.pacifistaGuildID == null) {
            System.out.print(ConsoleColors.GREEN + "Guild id de Pacifista:" + System.lineSeparator() + ConsoleColors.RESET);
            this.pacifistaGuildID = scanner.nextLine();
        }
        if (this.bienvenueID == null) {
            System.out.print(ConsoleColors.GREEN + "Channel id de bienvenue:" + System.lineSeparator() + ConsoleColors.RESET);
            this.bienvenueID = scanner.nextLine();
        }
        if (this.logID == null) {
            System.out.print(ConsoleColors.GREEN + "Channel id de log:" + System.lineSeparator() + ConsoleColors.RESET);
            this.logID = scanner.nextLine();
        }
        if (this.pacifistaChatID == null) {
            System.out.print(ConsoleColors.GREEN + "Channel id du chat pacifista:" + System.lineSeparator() + ConsoleColors.RESET);
            this.pacifistaChatID = scanner.nextLine();
        }
        if (this.playerID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade joueur:" + System.lineSeparator() + ConsoleColors.RESET);
            this.playerID = scanner.nextLine();
        }
        if (this.adminID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade admin:" + System.lineSeparator() + ConsoleColors.RESET);
            this.adminID = scanner.nextLine();
        }
        if (this.pacifistaAddress == null) {
            System.out.print(ConsoleColors.GREEN + "Adresse du socket du serveur minecraft:" + System.lineSeparator() + ConsoleColors.RESET);
            this.pacifistaAddress = scanner.nextLine();
        }
        if (this.pacifistaPort == 0) {
            System.out.print(ConsoleColors.GREEN + "Port du socket du serveur minecraft:" + System.lineSeparator() + ConsoleColors.WHITE);
            this.pacifistaPort = Integer.parseInt(scanner.nextLine());
        }

        Gson gson = new Gson();

        if (!dataFolder.exists() && !dataFolder.mkdir())
            throw new IOException("Error while creating data folder");
        if (!configFile.exists() && !configFile.createNewFile()) {
            throw new IOException("Erreur lors de la création de botConfiguration.json");
        }
        String objString = gson.toJson(this);
        FileActions.writeInFile(configFile, objString, false);
    }

    protected static BotConfiguration getConfiguration() throws IOException, NoSuchElementException {
        if (!dataFolder.exists() && !dataFolder.mkdir())
            throw new IOException("Error while creating data folder");
        if (!configFile.exists()) {
            BotConfiguration config = new BotConfiguration();
            config.checkConfig();
            return config;
        } else {
            String fileContent = FileActions.getFileContent(configFile);
            Gson gson = new Gson();
            BotConfiguration config = gson.fromJson(fileContent, BotConfiguration.class);
            config.checkConfig();
            return config;
        }
    }
}
