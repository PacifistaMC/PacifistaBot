package fr.pacifista.bot.pacifista;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.utils.BotException;
import fr.pacifista.bot.utils.ConsoleColors;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class SocketClientSpigot {

    private static volatile SocketClientSpigot instance = null;

    private final String ip;
    private final int port;
    private Socket socket;
    private boolean running = true;
    private InputStreamReader inputStreamReader;
    private BufferedReader reader;
    private PrintWriter writer;

    private SocketClientSpigot(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

    private void runSocket() {
        new Thread(() -> {

            while (this.running) {

                try {
                    this.socket = new Socket(ip, port);
                } catch (IOException exception) {
                    System.out.println(ConsoleColors.RED + "[Socket] - Impossible de se connecter, erreur: " + exception.getMessage() + ConsoleColors.RESET);
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                System.out.println(ConsoleColors.GREEN + "[Socket] - Socket connecté" + ConsoleColors.RESET);

                try {
                    inputStreamReader = new InputStreamReader(this.socket.getInputStream());
                    reader = new BufferedReader(inputStreamReader);
                    writer = new PrintWriter(this.socket.getOutputStream());

                    sendMessage("LOGIN DISCORD");
                    while (!socket.isClosed() && this.running) {
                        try {
                            readClient();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                } catch (IOException | BotException e) {
                    System.err.println(ConsoleColors.RED + "[BotException] - " + e.getMessage() + ConsoleColors.WHITE);
                } finally {
                    try {
                        if (inputStreamReader != null)
                            inputStreamReader.close();
                        if (reader != null)
                            reader.close();
                        if (writer != null)
                            writer.close();
                        if (socket != null && !socket.isClosed()) {
                            socket.close();
                            System.out.println(ConsoleColors.GREEN + "[Socket] - Socket déconnecté" + ConsoleColors.RESET);
                        }
                        Bot.refreshActivityMsg(-1);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void readClient() throws IOException {
        try {
            final String line = reader.readLine();
            if (line != null) {
                if (line.length() > 0) {
                    new Thread(() -> SpigotClientActions.onReceivedMessage(line)).start();
                }
            } else {
                this.socket.close();
            }
        } catch (SocketException e) {
            if (!this.socket.isClosed()) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(final String message) throws BotException {
        if (!running || socket == null || !socket.isConnected() || socket.isClosed())
            throw new BotException(BotException.PACIFISTA_SOCKET_NOT_CONNECTED);
        writer.write(message + System.lineSeparator());
        writer.flush();
    }

    private void closeConnexion() {
        try {
            this.running = false;
            this.socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void initSocket(final String ip, final int port) {
        if (instance != null)
            return;
        SocketClientSpigot socketClientSpigot = new SocketClientSpigot(ip, port);
        socketClientSpigot.runSocket();
        instance = socketClientSpigot;
    }

    private static SocketClientSpigot getInstance() {
        return instance;
    }

    public static void closeSocket() {
        SocketClientSpigot socket = getInstance();
        if (socket == null)
            return;
        socket.closeConnexion();
        instance = null;
    }

    public static void sendMessageToServer(final String message) throws BotException {
        if (instance == null)
            throw new BotException(BotException.PACIFISTA_SOCKET_NOT_CONNECTED);
        instance.sendMessage(message);
    }
}
