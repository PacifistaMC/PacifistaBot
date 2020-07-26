package fr.pacifista.bot.minecraftLink;

import fr.pacifista.bot.Modules.BotConfiguration;

public class PacifistaSocket {

    public static PacifistaInfos getInfos(BotConfiguration botConfiguration) {
        return new PacifistaInfos(botConfiguration);
    }
}
