package fr.pacifista.bot.discord.events;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogMessages extends ListenerAdapter {

    public LogMessages(final JDA jda) {
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final Member member = event.getMember();
        if (member == null || member.getUser().isBot()) return;

        log.info("[{}] {} : {}", event.getChannel().getName(), member.getEffectiveName(), event.getMessage().getContentRaw());
    }
}
