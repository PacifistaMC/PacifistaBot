package fr.pacifista.bot.Events;

import fr.pacifista.bot.Main;
import fr.pacifista.bot.Modules.BotActions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        User user = event.getUser();
        Member member = event.getMember();
        Guild guild = event.getGuild();
        OffsetDateTime creationDate = user.getTimeCreated();
        long nowTime = new Date().getTime() / 1000;
        long creationDateTime = creationDate.toInstant().getEpochSecond();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Bienvenue !");
        embedBuilder.setColor(new Color(67, 170, 139));
        embedBuilder.addField(":tada: Nouveau membre ! :tada:", user.getName(), false);
        embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
        embedBuilder.setThumbnail(user.getAvatarUrl());

        BotActions.sendMessageToChannel(embedBuilder.build(), Main.bot.getConfig().bienvenueID);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");
        embedBuilder.addField("Date de création", dateTimeFormatter.format(creationDate), false);

        long diff = nowTime - creationDateTime;
        if (diff <= 172800) {
            embedBuilder.addField(":warning: Attention :warning:", "Compte créé il y a moins de 2 jours !", false);
            embedBuilder.setColor(Color.orange);
            if (diff <= 600) {
                BotActions.sendMessageToChannel(
                        "<@&" + Main.bot.getConfig().adminID + "> :warning: Compte créé il y a moins de 10 minutes ! " + user.getAsTag(),
                        Main.bot.getConfig().logID
                );
            }
        }

        Role role = guild.getRoleById(Main.bot.getConfig().playerID);
        if (role != null)
            guild.addRoleToMember(event.getMember(), role).queue();

        BotActions.sendMessageToChannel(embedBuilder.build(), Main.bot.getConfig().logID);
        System.out.println(user.getAsTag() + " à rejoint le discord.");
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        User user = event.getUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(new Color(249, 65, 68));
        embedBuilder.setDescription(user.getAsTag() + " est parti du discord.");
        embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
        embedBuilder.setThumbnail(user.getAvatarUrl());

        BotActions.sendMessageToChannel(embedBuilder.build(), Main.bot.getConfig().logID);
        System.out.println(user.getAsTag() + " à quitté le discord.");
    }

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        User user = event.getUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setDescription(user.getAsTag() + " est banni du discord.");
        embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
        embedBuilder.setThumbnail(user.getAvatarUrl());

        BotActions.sendMessageToChannel(embedBuilder.build(), Main.bot.getConfig().logID);
        System.out.println(user.getAsTag() + " est banni du discord.");
    }

    @Override
    public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
        User user = event.getUser();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setDescription(user.getAsTag() + " n'est plus banni du discord.");
        embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
        embedBuilder.setThumbnail(user.getAvatarUrl());

        BotActions.sendMessageToChannel(embedBuilder.build(), Main.bot.getConfig().logID);
        System.out.println(user.getAsTag() + " n'est plus banni du discord.");
    }
}
