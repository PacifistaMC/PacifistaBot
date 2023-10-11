package fr.pacifista.bot.discord.config;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("discord.bot.config")
public class BotConfig {
    /**
     * Discord bot token get from discord app
     */
    private String botToken;

    /**
     * Pacifista discord
     */
    private String ticketsChannelId;
    private String ticketsCategoryId;
    private String ticketsLogsCategoryId;

    private String ticketsModRoleID;

    private String pacifistaApiToken;

    @Bean(destroyMethod = "shutdown")
    public JDA discordInstance() throws InterruptedException {
        final JDABuilder jdaBuilder = JDABuilder.createDefault(botToken);

        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        jdaBuilder.setActivity(Activity.of(
                Activity.ActivityType.WATCHING,
                "0 joueurs",
                "https://pacifista.fr"
        ));

        return jdaBuilder.build().awaitReady();
    }
}