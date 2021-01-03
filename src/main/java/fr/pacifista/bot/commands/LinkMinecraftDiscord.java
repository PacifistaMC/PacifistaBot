package fr.pacifista.bot.commands;

import com.google.gson.JsonObject;
import fr.pacifista.bot.pacifista.SocketClientSpigot;
import fr.pacifista.bot.pacifista.SocketDiscordClientCode;
import fr.pacifista.bot.utils.BotException;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.UUID;

public class LinkMinecraftDiscord extends BotCommand {

    public LinkMinecraftDiscord() {
        super("link");
    }

    @Override
    public void execute(User user, MessageChannel channel, List<String> args, final Message messageReceived) {
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            if (args.size() == 1) {
                try {
                    final UUID code = UUID.fromString(args.get(0));
                    final String userID = user.getId();
                    final JsonObject toSend = new JsonObject();

                    toSend.addProperty("code", SocketDiscordClientCode.LINK_MINECRAFT_AND_DISCORD);
                    toSend.addProperty("discordUserID", userID);
                    toSend.addProperty("generatedCode", code.toString());
                    SocketClientSpigot.sendMessageToServer(toSend.toString());
                    channel.sendMessage(":arrow_up: Envoi de la demande au serveur...").queue();
                } catch (IllegalArgumentException exception) {
                    channel.sendMessage(":warning: Le code que vous avez entré est invalide, veuillez vérifier le code.").queue();
                } catch (BotException e) {
                    channel.sendMessage(e.getPublicErrorMessage()).queue();
                    e.printStackTrace();
                }
            } else {
                channel.sendMessage(":warning: Utilisation de la commande : !link [code]\nPour obtenir le code vous devez vous connecter à Pacifista et entrer /discord link").queue();
            }
        } else {
            channel.sendMessage(":warning: Cette commande doit être utilisée en message privé.").queue();
            messageReceived.delete().queue();
        }
    }

    @Override
    public String getHelp() {
        return "Vous permet de lier votre compte Minecraft à votre compte Discord.\n\nVeuillez envoyer en message privé au PacifistaBot !link [code].\n\nVous pouvez obtenir le code en tapant /discord link en jeu.";
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
