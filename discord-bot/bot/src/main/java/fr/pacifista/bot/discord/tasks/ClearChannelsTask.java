package fr.pacifista.bot.discord.tasks;

import fr.pacifista.bot.core.exceptions.PacifistaBotException;
import fr.pacifista.bot.discord.config.BotConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class ClearChannelsTask {

    private final Set<TextChannel> channels = new HashSet<>();

    public ClearChannelsTask(final JDA jda,
                             final BotConfig botConfig) throws PacifistaBotException {
        final TextChannel pacifistaChatPublic = jda.getTextChannelById(botConfig.getPacifistaChatPublicId());
        if (pacifistaChatPublic == null) {
            throw new PacifistaBotException("Le channel de discussion publique pacifistachat n'a pas été trouvé.");
        }

        this.channels.add(pacifistaChatPublic);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearChannels() {
        final OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minusWeeks(2);

        for (TextChannel channel : this.channels) {
            Collection<Message> messages = channel.getHistory().retrievePast(100).complete();
            messages.removeIf(m -> m.getTimeCreated().isBefore(twoWeeksAgo));

            while (!messages.isEmpty()) {
                messages.removeIf(m -> m.getTimeCreated().isBefore(twoWeeksAgo));
                if (messages.isEmpty())
                    break;
                channel.deleteMessages(messages).complete();
                messages = channel.getHistory().retrievePast(100).complete();
            }
        }
    }

}
