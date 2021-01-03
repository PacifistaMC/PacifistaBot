package fr.pacifista.bot.commands;

import net.dv8tion.jda.api.entities.*;

import java.util.List;

public class LinkMinecraftDiscord extends BotCommand {

    public LinkMinecraftDiscord() {
        super("link");
    }

    @Override
    public void execute(Member user, MessageChannel channel, List<String> args, final Message messageReceived) {
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            if (args.size() >= 1)
            messageReceived.reply("yes !").queue();
        } else {
            channel.sendMessage(":warning: Cette commande doit être utilisée en message privé.").queue();
            messageReceived.delete().queue();
        }
    }

    @Override
    public String getHelp() {
        return "Vous permet de lier votre compte Minecraft à votre compte Discord. Veuillez envoyer en message privé au PacifistaBot !link [code]. Vous pouvez obtenir le code en tapant /discord link en jeu.";
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }

    @Override
    public boolean canExecuteInPrivateDM() {
        return true;
    }
}
