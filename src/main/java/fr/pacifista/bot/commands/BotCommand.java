package fr.pacifista.bot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
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

    public abstract void execute(final User user, final MessageChannel channel, final List<String> args, final Message messageReceived);

    public abstract String getHelp();

    public abstract boolean isPublic();

    public abstract boolean hasPermission(final Member member);

    public abstract boolean canExecuteInPrivateDM();

}
