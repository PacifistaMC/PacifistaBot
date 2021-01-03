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
    public String bienvenueChannelID;
    public String logChannelID;
    public String playerRoleID;
    public String donateurRoleID;
    public String aventurierRoleID;
    public String paladinRoleID;
    public String eliteRoleID;
    public String legendaireRoleID;
    public String adminRoleID;
    public String pacifistaChannelID;
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
        if (this.bienvenueChannelID == null) {
            System.out.print(ConsoleColors.GREEN + "Channel id de bienvenue:" + System.lineSeparator() + ConsoleColors.RESET);
            this.bienvenueChannelID = scanner.nextLine();
        }
        if (this.logChannelID == null) {
            System.out.print(ConsoleColors.GREEN + "Channel id de log:" + System.lineSeparator() + ConsoleColors.RESET);
            this.logChannelID = scanner.nextLine();
        }
        if (this.pacifistaChannelID == null) {
            System.out.print(ConsoleColors.GREEN + "Channel id du chat pacifista:" + System.lineSeparator() + ConsoleColors.RESET);
            this.pacifistaChannelID = scanner.nextLine();
        }
        if (this.playerRoleID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade joueur:" + System.lineSeparator() + ConsoleColors.RESET);
            this.playerRoleID = scanner.nextLine();
        }
        if (this.donateurRoleID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade donateur:" + System.lineSeparator() + ConsoleColors.RESET);
            this.donateurRoleID = scanner.nextLine();
        }
        if (this.aventurierRoleID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade aventurier:" + System.lineSeparator() + ConsoleColors.RESET);
            this.aventurierRoleID = scanner.nextLine();
        }
        if (this.paladinRoleID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade paladin:" + System.lineSeparator() + ConsoleColors.RESET);
            this.paladinRoleID = scanner.nextLine();
        }
        if (this.eliteRoleID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade elite:" + System.lineSeparator() + ConsoleColors.RESET);
            this.eliteRoleID = scanner.nextLine();
        }
        if (this.legendaireRoleID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade legendaire:" + System.lineSeparator() + ConsoleColors.RESET);
            this.legendaireRoleID = scanner.nextLine();
        }
        if (this.adminRoleID == null) {
            System.out.print(ConsoleColors.GREEN + "Group id du grade admin:" + System.lineSeparator() + ConsoleColors.RESET);
            this.adminRoleID = scanner.nextLine();
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
