package fr.pacifista.bot.discord;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j(topic = "PacifistaBot")
@ConfigurationProperties("discord.bot.config")
public class PacifistaBot {

    /**
     * Discord bot token get from discord app
     */
    private String botToken;

    @Bean(destroyMethod = "shutdown")
    public JDA discordInstance() throws InterruptedException {
        final JDABuilder jdaBuilder = JDABuilder.createDefault(botToken);

        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.setActivity(Activity.of(
                Activity.ActivityType.WATCHING,
                "0 joueurs",
                "https://pacifista.fr"
        ));

        log.info("Starting discord bot...");
        return jdaBuilder.build().awaitReady();
    }

}
