package fr.pacifista.bot.core.giveaways.entities;

import fr.pacifista.bot.core.giveaways.enums.GiveawayType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Giveaway {
    private UUID giveawayId;
    private Date createdAt;
    private String prize;
    private String pacifistaCommandToSend;
    private int winners = 1;
    private List<String> participantsIds = new ArrayList<>();
    private String discordMessageId;
    private GiveawayType giveawayType;

    public void addParticipant(String participantId) {
        this.participantsIds.add(participantId);
    }

    public void removeParticipant(String participantId) {
        this.participantsIds.remove(participantId);
    }
}
