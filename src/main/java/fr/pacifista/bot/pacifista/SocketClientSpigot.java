package fr.pacifista.bot.pacifista;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.utils.BotException;
import fr.pacifista.bot.utils.ConsoleColors;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class SocketClientSpigot {

    private static volatile SocketClientSpigot instance = null;

    private final String ip;
    private final int port;
    private Socket socket;
    private boolean running = true;
    private BufferedInputStream reader;
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

                    reader = new BufferedInputStream(this.socket.getInputStream());
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
            }
        }).start();
    }

    private void readClient() throws IOException {
        try {
            byte[] messageByte = new byte[10000];
            int stream = reader.read(messageByte);
            if (stream == -1) {
                this.socket.close();
                return;
            }
            final String message = new String(messageByte, 0, stream).replaceAll("\\r\\n|\\r|\\n", "");
            new Thread(() -> SpigotClientActions.onReceivedMessage(message)).start();
        } catch (SocketException e) {
            if (e.getMessage().equals("Socket closed")) return;
            e.printStackTrace();
        }
    }

    private void sendMessage(final String message) throws BotException {
        if (!running || socket == null || !socket.isConnected() || socket.isClosed())
            throw new BotException(BotException.PACIFISTA_SOCKET_NOT_CONNECTED);
        writer.write(message);
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
