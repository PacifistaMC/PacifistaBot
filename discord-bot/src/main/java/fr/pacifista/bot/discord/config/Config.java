package fr.pacifista.bot.discord.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {
    private String ticketsChannelId;
    private String ticketsCategoryId;
    private String ticketsLogsCategoryId;

    private String ticketsModRoleID;

    private String pacifistaApiToken;
}