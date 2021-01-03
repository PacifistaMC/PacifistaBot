package fr.pacifista.bot.events;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.commands.BotCommand;
import fr.pacifista.bot.commands.ClearChat;
import fr.pacifista.bot.commands.IpCommand;
import fr.pacifista.bot.commands.LogsCommand;
import fr.pacifista.bot.modules.Log;
import fr.pacifista.bot.pacifista.SpigotClientActions;
import fr.pacifista.bot.utils.BotException;
import fr.pacifista.bot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMessage extends ListenerAdapter {

    private final List<BotCommand> commands = Arrays.asList(
            new IpCommand(),
            new LogsCommand(),
            new ClearChat()
    );

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        User user = e.getAuthor();
        Member member = e.getMember();
        TextChannel channel = e.getChannel();
        Message message = e.getMessage();

        try {
            if (channel.getId().equals(Bot.getConfiguration().pacifistaChatID) && member != null) {
                SpigotClientActions.sendDiscordMessageToPacifista(member, message, channel);
                return;
            }
        } catch (BotException botException) {
            botException.printStackTrace();
        }

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

        if (command.equalsIgnoreCase("help")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setDescription("Voici les commandes du bot !");
            embedBuilder.setColor(Utils.MAIN_COLOR);
            for (BotCommand botCommand : commands) {
                if (botCommand.isPublic() && botCommand.hasPermission(member))
                    embedBuilder.addField("!" + botCommand.getCommandName(), botCommand.getHelp(), true);
            }
            channel.sendMessage(embedBuilder.build()).queue();
            return;
        }

        for (BotCommand botCommand : commands) {
            if (command.equalsIgnoreCase(botCommand.getCommandName())) {
                if (botCommand.hasPermission(member))
                    botCommand.execute(member, channel, args);
                else
                    channel.sendMessage(":warning: Vous n'avez pas la permission d'executer cette commande.").queue();
                return;
            }
        }
        channel.sendMessage(":warning: La commande ``" + command + "`` n'existe pas.\n``!help pour obtenir la liste des commandes``").queue();
    }
}
