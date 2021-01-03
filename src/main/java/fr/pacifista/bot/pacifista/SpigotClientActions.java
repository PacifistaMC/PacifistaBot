package fr.pacifista.bot.pacifista;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import fr.pacifista.bot.Bot;
import fr.pacifista.bot.BotConfiguration;
import fr.pacifista.bot.utils.BotException;
import fr.pacifista.bot.utils.ConsoleColors;
import fr.pacifista.bot.utils.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import static fr.pacifista.bot.pacifista.SocketDiscordClientCode.*;

public class SpigotClientActions {

    protected static void onReceivedMessage(final String response) {
        try {
            final BotConfiguration botConfiguration = Bot.getConfiguration();
            JsonObject res = JsonParser.parseString(response).getAsJsonObject();

            switch (res.get("type").getAsString()) {
                case FETCH_PLAYER_DATA:
                    fetchPlayersPacifista(res);
                    break;
                case Events.PLAYER_CHAT:
                    final String playerName = res.get("name").getAsString();
                    final String message = res.get("message").getAsString();

                    final StringBuilder str = new StringBuilder();
                    for (int i = 0; i < message.length(); ++i) {
                        if (message.charAt(i) == '§' || (i > 0 && message.charAt(i - 1) == '§'))
                            continue;
                        str.append(message.charAt(i));
                    }
                    Bot.sendMessageToChannel("**__" + playerName + "__**" + " » " + str.toString() + "", Bot.getConfiguration().pacifistaChannelID);
                    break;
                case SEND_MESSAGE_TO_DISCORD_USER:
                    final String userID = res.get("userID").getAsString();
                    final String messageToUser = res.get("message").getAsString();

                    Bot.sendPrivateMessage(userID, messageToUser);
                    break;
                case SET_PACIFISTA_GRADE_TO_DISCORD:
                    final String userIDDiscord = res.get("userDiscordID").getAsString();
                    final String rankName = res.get("rankName").getAsString();

                    Bot.updateRole(botConfiguration.donateurRoleID, userIDDiscord, false);
                    Bot.updateRole(botConfiguration.aventurierRoleID, userIDDiscord, false);
                    Bot.updateRole(botConfiguration.paladinRoleID, userIDDiscord, false);
                    Bot.updateRole(botConfiguration.eliteRoleID, userIDDiscord, false);
                    Bot.updateRole(botConfiguration.legendaireRoleID, userIDDiscord, false);
                    if (rankName.equalsIgnoreCase("Donateur"))
                        Bot.updateRole(botConfiguration.donateurRoleID, userIDDiscord, true);
                    else if (rankName.equalsIgnoreCase("Aventurier"))
                        Bot.updateRole(botConfiguration.aventurierRoleID, userIDDiscord, true);
                    else if (rankName.equalsIgnoreCase("Paladin"))
                        Bot.updateRole(botConfiguration.paladinRoleID, userIDDiscord, true);
                    else if (rankName.equalsIgnoreCase("Elite"))
                        Bot.updateRole(botConfiguration.eliteRoleID, userIDDiscord, true);
                    else if (rankName.equalsIgnoreCase("Legendaire") || rankName.equalsIgnoreCase("Ami"))
                        Bot.updateRole(botConfiguration.legendaireRoleID, userIDDiscord, true);
                    break;
                case UNLINK_MINECRAFT_AND_DISCORD:
                    final String userDiscordID = res.get("userDiscordID").getAsString();

                    Bot.updateRole(botConfiguration.donateurRoleID, userDiscordID, false);
                    Bot.updateRole(botConfiguration.aventurierRoleID, userDiscordID, false);
                    Bot.updateRole(botConfiguration.paladinRoleID, userDiscordID, false);
                    Bot.updateRole(botConfiguration.eliteRoleID, userDiscordID, false);
                    Bot.updateRole(botConfiguration.legendaireRoleID, userDiscordID, false);
                    break;
            }
        } catch (IllegalStateException | JsonSyntaxException | NullPointerException ignored) {
        } catch (BotException e) {
            e.printStackTrace();
        }
    }

    private static void fetchPlayersPacifista(final JsonObject data) {
        final int playerCount = data.get("playerCount").getAsInt();
        /*final JsonArray playersData = data.get("data").getAsJsonArray();
        final int arraySize = playersData.size();*/

        Bot.refreshActivityMsg(playerCount);
        /*for (int i = 0; i < arraySize; ++i) {
            final JsonObject playerData = playersData.get(i).getAsJsonObject();
            final String playerName = playerData.get("playerName").getAsString();
            final UUID playerUUID = UUID.fromString(playerData.get("uuid").getAsString());
            final String serverName = playerData.get("server").getAsString();
            final int playerPing = playerData.get("ping").getAsInt();
        }*/
    }

    public static void sendDiscordMessageToPacifista(final Member user, final Message message, final MessageChannel channel) {
        if (message.getAttachments().size() > 0 || Utils.isStringContainUrl(message.getContentRaw())) {
            channel.sendMessage(":warning: ``Pas d'image, d'url ou de vidéos dans ce canal``").queue();
            message.delete().queue();
            return;
        }

        final JsonObject toSend = new JsonObject();
        final JsonObject userJson = new JsonObject();

        toSend.addProperty("code", DISCORD_CHAT_TO_PACIFISTA);
        userJson.addProperty("id", user.getId());
        userJson.addProperty("name", user.getNickname() == null ? user.getUser().getName() : user.getNickname());
        userJson.addProperty("userTag", user.getUser().getAsTag());
        toSend.addProperty("message", message.getContentDisplay());
        toSend.add("user", userJson);

        try {
            SocketClientSpigot.sendMessageToServer(toSend.toString());
        } catch (BotException e) {
            channel.sendMessage(e.getPublicErrorMessage()).queue();
            System.err.println(ConsoleColors.RED + "[BotException] - " + e.getMessage() + ConsoleColors.WHITE);
        }
    }

}
