package fr.pacifista.bot.discord;

import fr.pacifista.bot.core.GiveawaysManager;
import fr.pacifista.bot.discord.config.BotConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Slf4j(topic = "PacifistaBot")
@Configuration
public class PacifistaBot {
    private final JDA jda;
    private final BotConfig botConfig;
    private final GiveawaysManager giveawaysManager = new GiveawaysManager();

    public PacifistaBot(BotConfig botConfig, JDA jda) {
        try {
            this.botConfig = botConfig;
            this.jda = jda;

            log.info("Bot prêt !");
        } catch (Exception e) {
            log.error("Une erreur est survenue lors du lancement du bot discord. {}", e.getMessage());
            throw e;
        }
    }

    @Bean
    public PacifistaBot getPacifistaBot() {
        return this;
    }
}
