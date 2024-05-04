package fr.pacifista.bot.discord.modules.giveaway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("discord.bot.config.giveaway")
public class BotGiveawayConfig {

    private String giveawaysChannelId;

    private String giveawaysRoleId;

}
