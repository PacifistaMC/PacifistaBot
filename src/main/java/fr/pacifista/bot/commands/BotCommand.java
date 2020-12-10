package fr.pacifista.bot.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public abstract class BotCommand {

    private final String commandName;

    protected BotCommand(final String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public abstract void execute(final User user, final TextChannel channel, final List<String> args);

    public abstract String getHelp();

    public abstract boolean isPublic();

}
