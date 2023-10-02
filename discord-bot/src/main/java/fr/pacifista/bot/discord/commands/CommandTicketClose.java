package fr.pacifista.bot.discord.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;

@Service
public class CommandTicketClose extends Command {

    public CommandTicketClose(JDA jda) {
        super(jda);
    }

    @Override
    public String getCommandName() {
        return "close";
    }

    @Override
    public String getCommandDescription() {
        return "Ferme le ticket actuel !";
    }

    @Override
    public void onCommand(@NonNull SlashCommandInteractionEvent interactionEvent) {
        Channel channel = interactionEvent.getChannel();

        if (channel.getType() != ChannelType.TEXT || !channel.getName().contains("ticket-")) {
            interactionEvent.reply(":warning: Ce salon n'est pas un ticket !").queue();
        }

        Button button = Button.danger("ticket-close", "Fermer le ticket")
                .withEmoji(Emoji.fromUnicode("⚠️"));

        interactionEvent.reply("Êtes vous sûr de vouloir fermer ce ticket ? Il ne sera plus accessible.")
                .addActionRow(button)
                .queue();
    }
}
