package fr.pacifista.bot.discord.events;

import fr.pacifista.bot.core.GiveawaysManager;
import fr.pacifista.bot.core.entities.giveaways.Giveaway;
import fr.pacifista.bot.core.entities.giveaways.enums.GiveawayType;
import fr.pacifista.bot.discord.PacifistaBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotReactionsEvents extends ListenerAdapter {
    private final GiveawaysManager giveawaysManager;

    public BotReactionsEvents(PacifistaBot pacifistaBot, GiveawaysManager giveawaysManager) {
        this.giveawaysManager = giveawaysManager;
        pacifistaBot.getJda().addEventListener(this);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Member member = event.retrieveMember().complete();
        if (member.getUser().isBot()) return;
        Giveaway giveaway = isGiveaway(event.getMessageId());
        if (giveaway != null) {
            String memberId = member.getId();
            giveaway.addParticipant(memberId);
            this.giveawaysManager.updateGiveaway(giveaway);
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        Member member = event.retrieveMember().complete();
        if (member.getUser().isBot()) return;
        Giveaway giveaway = isGiveaway(event.getMessageId());
        if (giveaway != null) {
            String memberId = member.getId();
            giveaway.removeParticipant(memberId);
            this.giveawaysManager.updateGiveaway(giveaway);
        }
    }

    private Giveaway isGiveaway(String messageId) {
        List<Giveaway> giveawayList = this.giveawaysManager.getGiveaways();

        for (Giveaway giveaway : giveawayList) {
            if (    giveaway.getGiveawayType().equals(GiveawayType.DISCORD) &&
                    giveaway.getDiscordMessageId().equals(messageId)) {
                return giveaway;
            }
        }

        return null;
    }
}
