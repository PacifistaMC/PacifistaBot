package fr.pacifista.bot.events;

import fr.pacifista.bot.Bot;
import fr.pacifista.bot.BotConfiguration;
import fr.pacifista.bot.utils.BotException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class UserJoinLeave extends ListenerAdapter {

    private final Bot bot;
    private final BotConfiguration botConfiguration;

    public UserJoinLeave(final Bot bot, final BotConfiguration botConfiguration) {
        this.bot = bot;
        this.botConfiguration = botConfiguration;
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        User userJoined = event.getUser();
        Guild guild = event.getGuild();
        OffsetDateTime creationDate = userJoined.getTimeCreated();
        long nowTime = new Date().getTime() / 1000;
        long creationDateTime = creationDate.toInstant().getEpochSecond();

        if (!guild.getId().equals(botConfiguration.pacifistaGuildID))
            return;
        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Bienvenue !");
            embedBuilder.setColor(new Color(67, 170, 139));
            embedBuilder.addField(":tada: Nouveau membre ! :tada:", userJoined.getName(), false);
            embedBuilder.setAuthor(userJoined.getAsTag(), null, userJoined.getAvatarUrl());
            embedBuilder.setThumbnail(userJoined.getAvatarUrl());

            Bot.sendMessageToChannel(embedBuilder.build(), botConfiguration.bienvenueChannelID);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");
            embedBuilder.addField("Date de création", dateTimeFormatter.format(creationDate), false);

            long diff = nowTime - creationDateTime;
            if (diff <= 172800) {
                embedBuilder.addField(":warning: Attention :warning:", "Compte créé il y a moins de 2 jours !", false);
                embedBuilder.setColor(Color.orange);
                if (diff <= 600) {
                    Bot.sendMessageToChannel(
                            "<@&" + botConfiguration.adminRoleID + "> :warning: Compte créé il y a moins de 10 minutes ! " + userJoined.getAsTag(),
                            botConfiguration.logChannelID
                    );
                }
            }

            Role role = guild.getRoleById(botConfiguration.playerRoleID);
            if (role != null)
                guild.addRoleToMember(event.getMember(), role).queue();

            Bot.sendMessageToChannel(embedBuilder.build(), botConfiguration.logChannelID);
            System.out.println(userJoined.getAsTag() + " à rejoint le discord.");
        } catch (BotException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        User userLeave = event.getUser();

        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(new Color(249, 65, 68));
            embedBuilder.setDescription(userLeave.getAsTag() + " est parti du discord.");
            embedBuilder.setAuthor(userLeave.getAsTag(), null, userLeave.getAvatarUrl());
            embedBuilder.setThumbnail(userLeave.getAvatarUrl());

            Bot.sendMessageToChannel(embedBuilder.build(), botConfiguration.logChannelID);
            System.out.println(userLeave.getAsTag() + " à quitté le discord.");
        } catch (BotException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        User bannedUser = event.getUser();

        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.CYAN);
            embedBuilder.setDescription(bannedUser.getAsTag() + " est banni du discord.");
            embedBuilder.setAuthor(bannedUser.getAsTag(), null, bannedUser.getAvatarUrl());
            embedBuilder.setThumbnail(bannedUser.getAvatarUrl());

            Bot.sendMessageToChannel(embedBuilder.build(), botConfiguration.logChannelID);
            System.out.println(bannedUser.getAsTag() + " est banni du discord.");
        } catch (BotException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
        User unbannedUser = event.getUser();

        try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.CYAN);
            embedBuilder.setDescription(unbannedUser.getAsTag() + " n'est plus banni du discord.");
            embedBuilder.setAuthor(unbannedUser.getAsTag(), null, unbannedUser.getAvatarUrl());
            embedBuilder.setThumbnail(unbannedUser.getAvatarUrl());

            Bot.sendMessageToChannel(embedBuilder.build(), botConfiguration.logChannelID);
            System.out.println(unbannedUser.getAsTag() + " n'est plus banni du discord.");
        } catch (BotException e) {
            e.printStackTrace();
        }
    }
}
