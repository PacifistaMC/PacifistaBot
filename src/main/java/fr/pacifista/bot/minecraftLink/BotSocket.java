package fr.pacifista.bot.minecraftLink;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.Main;
import fr.pacifista.bot.Modules.BotActions;
import fr.pacifista.bot.Utils.ConsoleColors;
import fr.pacifista.bot.Utils.FileActions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class BotSocket extends Thread {

    private boolean isRunning = true;
    private ServerSocket serverSocket = null;
    private final Bot bot;

    public BotSocket(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(bot.getConfig().botSocketPort);
            System.out.println(ConsoleColors.GREEN + "[SOCKET] - Serveur fonctionnel sur le port " + bot.getConfig().botSocketPort);
            while (isRunning && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                InetAddress ip = socket.getInetAddress();
                String ipClient = ip.toString().substring(1);
                if (!canConnect(ipClient)) {
                    System.out.println(ConsoleColors.RED + "[SOCKET] - IP non autorisée: " + ipClient);
                    socket.close();
                    continue;
                }
                new Thread(new Client(socket)).start();
            }
        } catch (IOException e) {
            if (e instanceof SocketException) {
                SocketException socketException = (SocketException) e;
                String message = socketException.getMessage();
                if (message != null && message.equalsIgnoreCase("Socket closed")) return;
            }
            e.printStackTrace();
        } finally {
            stopSocket();
        }
    }

    public void stopSocket() {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println(ConsoleColors.GREEN + "[SOCKET] - Serveur fermé");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean canConnect(String ip) {
        if (ip.startsWith("10.") || ip.startsWith("172.") || ip.startsWith("192.") || ip.startsWith("127."))
            return true;
        else {
            String fileContents;
            try {
                File file = new File(Main.dataFolder, "whitelist_socket.txt");
                if (file.exists()) {
                    fileContents = FileActions.getFileContent(file);
                } else {
                    if (!file.createNewFile())
                        throw new IOException("Cannot create whitelist_socket.txt");
                    return false;
                }

                List<String> whitelistIP = new ArrayList<>();
                char[] str = fileContents.toCharArray();
                StringBuilder buffer = new StringBuilder();
                for (char c : str) {
                    if ((c > '9' || c < '0') && c != '.' && c != '\n')
                        continue;
                    if (c == '\n' && buffer.length() > 0) {
                        whitelistIP.add(buffer.toString());
                        buffer.setLength(0);
                    } else
                        buffer.append(c);
                }
                if (buffer.length() > 0)
                    whitelistIP.add(buffer.toString());
                return whitelistIP.contains(ip);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
       }
    }
}

class Client implements Runnable {

    private boolean isRunning = true;
    private final Socket socket;
    private PrintWriter writer = null;
    private BufferedInputStream reader = null;

    Client(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (isRunning && !socket.isClosed()) {
                writer = new PrintWriter(socket.getOutputStream());
                reader = new BufferedInputStream(socket.getInputStream());
                String response = readClient();

                if (response.startsWith("SPIGOT_MESSAGE")) {
                    char[] str = response.toCharArray();
                    StringBuilder buffer = new StringBuilder();
                    for (int i = 14; i < str.length; ++i) {
                        if (str[i - 1] != '§' && str[i] != '§')
                            buffer.append(str[i]);
                    }
                    BotActions.sendMessageToChannel(buffer.toString(), Main.instance.getBot().getConfig().pacifistaChatID);
                    isRunning = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed())
                    socket.close();
                if (writer != null)
                    writer.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readClient() throws IOException {
        byte[] messageBytes = new byte[10000];
        int stream = reader.read(messageBytes);
        return new String(messageBytes, 0, stream);
    }
}