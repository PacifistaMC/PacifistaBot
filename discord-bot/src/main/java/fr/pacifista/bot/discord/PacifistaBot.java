package fr.pacifista.bot.discord;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("discord.bot.config")
public class PacifistaBot {

    /**
     * Discord bot token get from discord app
     */
    private String botToken;

}
