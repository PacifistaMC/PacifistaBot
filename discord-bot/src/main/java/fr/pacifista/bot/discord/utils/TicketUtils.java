package fr.pacifista.bot.discord.utils;

import fr.pacifista.bot.discord.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

@Service
public class TicketUtils {
    private final JDA jda;
    private final Config config;

    public TicketUtils(final JDA jda, Config config) {
        this.jda = jda;
        this.config = config;

        sendTicketMessage();
    }

    public void sendTicketMessage() {
        TextChannel channel = jda.getTextChannelById(this.config.getTicketsChannelId());

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.PACIFISTA_COLOR)
                .setTitle("🎫 Tickets")
                .setDescription("Besoin de nous contacter ? Clique sur le bouton ci-dessous pour créer un ticket !");

        Button button = Button.primary("ticket-create", "Créer un ticket");

        channel.sendMessageEmbeds(embed.build()).addActionRow(button).queue();
    }
}
