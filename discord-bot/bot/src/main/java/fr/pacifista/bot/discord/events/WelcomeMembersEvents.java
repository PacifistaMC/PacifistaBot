package fr.pacifista.bot.discord.events;

import fr.pacifista.bot.core.exceptions.PacifistaBotException;
import fr.pacifista.bot.discord.config.BotConfig;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@Service
public class WelcomeMembersEvents extends ListenerAdapter {

    private final Role playerRole;
    private final TextChannel welcomeChannel;
    private final TextChannel logChannel;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy HH:mm");


    public WelcomeMembersEvents(final JDA jda,
                                final BotConfig botConfig) throws PacifistaBotException {
        jda.addEventListener(this);

        this.playerRole = jda.getRoleById(botConfig.getPlayerRoleId());
        if (playerRole == null) {
            throw new PacifistaBotException("Le rôle joueur n'a pas été trouvé.");
        }
        this.welcomeChannel = jda.getTextChannelById(botConfig.getWelcomeChannelId());
        if (welcomeChannel == null) {
            throw new PacifistaBotException("Le channel de bienvenue n'a pas été trouvé.");
        }
        this.logChannel = jda.getTextChannelById(botConfig.getDiscordChannelLogId());
        if (logChannel == null) {
            throw new PacifistaBotException("Le channel de log n'a pas été trouvé.");
        }
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        final User userJoined = event.getUser();
        final Guild guild = event.getGuild();

        guild.addRoleToMember(event.getMember(), this.playerRole).queue();

        final OffsetDateTime creationDate = userJoined.getTimeCreated();
        long nowTime = new Date().getTime() / 1000;
        long creationDateTime = creationDate.toInstant().getEpochSecond();

        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Bienvenue !");
        embedBuilder.setColor(new Color(67, 170, 139));
        embedBuilder.addField(":tada: Nouveau membre ! :tada:", userJoined.getName(), false);
        embedBuilder.setAuthor(userJoined.getName(), null, userJoined.getAvatarUrl());
        embedBuilder.setThumbnail(userJoined.getAvatarUrl());
        welcomeChannel.sendMessageEmbeds(embedBuilder.build()).queue();

        embedBuilder.addField("Date de création", dateTimeFormatter.format(creationDate), false);
        long diff = nowTime - creationDateTime;
        if (diff <= 172800) {
            embedBuilder.addField(":warning: Attention :warning:", "Compte créé il y a moins de 2 jours !", false);
            embedBuilder.setColor(Color.orange);
            if (diff <= 600) {
                logChannel.sendMessage(":warning: Compte créé il y a moins de 10 minutes ! " + userJoined.getName() + " id: " + userJoined.getId()).queue();
            }
        }

        embedBuilder.addField("ID", userJoined.getId(), false);
        logChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        log.info("{} a rejoint le discord.", userJoined.getName());
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        final User userLeave = event.getUser();
        final EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(new Color(249, 65, 68));
        embedBuilder.setDescription(userLeave.getName() + " est parti du discord.");
        embedBuilder.setAuthor(userLeave.getName(), null, userLeave.getAvatarUrl());
        embedBuilder.setThumbnail(userLeave.getAvatarUrl());

        logChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        log.info("{} a quitté le discord.", userLeave.getName());
    }

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        final User bannedUser = event.getUser();
        final EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setDescription(bannedUser.getName() + " est banni du discord.");
        embedBuilder.setAuthor(bannedUser.getName(), null, bannedUser.getAvatarUrl());
        embedBuilder.setThumbnail(bannedUser.getAvatarUrl());
        logChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        log.info("{} est banni du discord.", bannedUser.getName());
    }

    @Override
    public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
        final User unbannedUser = event.getUser();
        final EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setDescription(unbannedUser.getName() + " n'est plus banni du discord.");
        embedBuilder.setAuthor(unbannedUser.getName(), null, unbannedUser.getAvatarUrl());
        embedBuilder.setThumbnail(unbannedUser.getAvatarUrl());

        logChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        log.info("{} n'est plus banni du discord.", unbannedUser.getName());
    }

}
