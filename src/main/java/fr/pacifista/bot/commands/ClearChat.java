package fr.pacifista.bot.commands;

import fr.pacifista.bot.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class ClearChat extends BotCommand {
    public ClearChat() {
        super("clearChat");
    }

    @Override
    public void execute(Member user, TextChannel channel, List<String> args) {
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
}
