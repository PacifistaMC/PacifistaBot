package fr.pacifista.bot.events;

import fr.pacifista.bot.modules.Log;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMessage extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        User user = e.getAuthor();
        TextChannel channel = e.getChannel();
        Message message = e.getMessage();

        Log.logMessage(user, channel, message.getContentRaw());

        List<String> args = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message.getContentRaw());
        while (m.find())
            args.add(m.group(1));
        if (args.size() < 1) return;
        String command = args.get(0);
        args.remove(0);
        if (!command.startsWith("!")) return;
        command = command.substring(1);

        try {
            Method[] commandList = Commands.class.getDeclaredMethods();
            for (Method method : commandList) {
                String methodName = method.getName();
                if (methodName.equalsIgnoreCase(command)) {
                    method.invoke(Commands.class, user, channel, args);
                    return;
                }
            }
            channel.sendMessage(":warning: La commande ``" + command + "`` n'existe pas.\n``!help pour obtenir la liste des commandes``").queue();
        } catch (InvocationTargetException | IllegalAccessException noSuchMethodException) {
            noSuchMethodException.printStackTrace();
        }
    }
}
