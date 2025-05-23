package fr.pacifista.bot.discord.modules.giveaway.events;

import fr.pacifista.bot.core.giveaways.entities.Giveaway;
import fr.pacifista.bot.core.giveaways.services.GiveawaysService;
import fr.pacifista.bot.discord.modules.giveaway.entities.DiscordGiveaway;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotReactionsEvents extends ListenerAdapter {
    private final GiveawaysService giveawaysManager;

    public BotReactionsEvents(JDA jda, GiveawaysService giveawaysManager) {
        this.giveawaysManager = giveawaysManager;
        jda.addEventListener(this);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        final Member member = event.retrieveMember().complete();
        if (member.getUser().isBot()) return;

        final DiscordGiveaway giveaway = isGiveaway(event.getMessageId());
        if (giveaway != null) {
            giveaway.addParticipant(member.getId());
            this.giveawaysManager.updateGiveaway(giveaway);
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        final Member member = event.retrieveMember().complete();
        if (member.getUser().isBot()) return;

        final Giveaway giveaway = isGiveaway(event.getMessageId());
        if (giveaway != null) {
            giveaway.removeParticipant(member.getId());
            this.giveawaysManager.updateGiveaway(giveaway);
        }
    }

    private DiscordGiveaway isGiveaway(String messageId) {
        List<Giveaway> giveawayList = this.giveawaysManager.getGiveaways();

        for (Giveaway giveaway : giveawayList) {
            if (giveaway instanceof final DiscordGiveaway discordGiveaway) {

            }
            if (giveaway.getGiveawayType().equals(GiveawayType.DISCORD) &&
                giveaway.getDiscordMessageId().equals(messageId)) {
                return giveaway;
            }
        }

        return null;
    }
}
