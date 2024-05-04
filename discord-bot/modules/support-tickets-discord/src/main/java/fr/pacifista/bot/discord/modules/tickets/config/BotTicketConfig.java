package fr.pacifista.bot.discord.modules.tickets.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("discord.bot.config.tickets")
public class BotTicketConfig {

    private String ticketsChannelId;
    private String ticketsCategoryId;
    private String ticketsLogsChannelId;

    private String ticketsModRoleId;

}
