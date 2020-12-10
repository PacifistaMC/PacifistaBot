package fr.pacifista.bot.commands;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.modules.ConsoleCommands;
import fr.pacifista.bot.utils.BotException;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class LogsCommand extends BotCommand {

    public LogsCommand() {
        super("logs");
    }

    @Override
    public void execute(User user, TextChannel channel, List<String> args) {
        try {
            if (!channel.getId().equals(Bot.getConfiguration().logID)) return;
            ConsoleCommands.getLogs(args);
        } catch (BotException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public boolean isPublic() {
        return false;
    }
}
