package fr.pacifista.bot.commands;

import fr.pacifista.bot.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

public class ClearChat extends BotCommand {
    public ClearChat() {
        super("clearChat");
    }

    @Override
    public void execute(User user, MessageChannel channel, List<String> args, final Message messageReceived) {
        if (!channel.getType().isGuild()) return;
        Bot.clearChannel(channel.getId());
    }

    @Override
    public String getHelp() {
        return "Clear le chat actuel";
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.hasPermission(Permission.MESSAGE_MANAGE);
    }

    @Override
    public boolean canExecuteInPrivateDM() {
        return false;
    }
}