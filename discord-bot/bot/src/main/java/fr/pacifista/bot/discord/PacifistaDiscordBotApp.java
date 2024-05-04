package fr.pacifista.bot.discord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
        "com.funixproductions",
        "fr.pacifista"
})
@EnableFeignClients(basePackages = {
        "fr.pacifista",
        "com.funixproductions"
})
@EnableScheduling
public class PacifistaDiscordBotApp {
    public static void main(String[] args) {
        SpringApplication.run(PacifistaDiscordBotApp.class, args);
    }
}
