package fr.pacifista.bot.commands;

import fr.pacifista.bot.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class IpCommand extends BotCommand {

    public IpCommand() {
        super("ip");
    }

    @Override
    public void execute(Member user, TextChannel channel, List<String> args) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Pacifista Minecraft", "https://pacifista.fr");
        embedBuilder.setDescription("Serveur minecraft survie");
        embedBuilder.addField("Site web", "https://pacifista.fr", true);
        embedBuilder.addField("IP de connexion", "play.pacifista.fr", true);
        embedBuilder.addField("Version", "1.16.4", true);
        embedBuilder.setColor(Utils.MAIN_COLOR);

        channel.sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String getHelp() {
        return "Vous donne les informations du serveur Pacifista !";
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean hasPermission(Member member) {
        return true;
    }
}
