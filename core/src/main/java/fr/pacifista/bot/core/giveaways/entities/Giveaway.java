package fr.pacifista.bot.core.giveaways.entities;

import fr.pacifista.bot.core.files.entities.FileGsonData;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class Giveaway extends FileGsonData {

    /**
     * Le nom du giveaway
     */
    @NonNull
    private String name;

    /**
     * La description du giveaway
     */
    @NonNull
    private String description;

    /**
     * La commande Pacifista à sur le serveur mc
     */
    @NonNull
    private String pacifistaCommandToSend;

    /**
     * La liste des participants au giveaway
     */
    @NonNull
    private Set<GiveawayUser> participants;

    /**
     * Le créateur du giveaway
     */
    @Nullable
    private GiveawayUser creator;

    /**
     * L'id du canal si besoin, comme un message id sur Discord ou un channel id sur twitch
     */
    @Nullable
    private String canalId;

    /**
     * Le nombre de gagnants pour le giveaway
     */
    private int winners;

    public Giveaway(@NotNull final String name, @NotNull final String description, @NotNull final String pacifistaCommandToSend) {
        super();
        this.name = name;
        this.description = description;
        this.pacifistaCommandToSend = pacifistaCommandToSend;

        this.participants = new HashSet<>();
        this.winners = 1;
    }

    public void addParticipant(GiveawayUser participant) {
        this.participants.add(participant);
    }

    @Nullable
    public GiveawayUser getParticipant(final String participantId) {
        for (GiveawayUser participant : this.participants) {
            if (participant.getUserId().equals(participantId)) {
                return participant;
            }
        }

        return null;
    }

    public void removeParticipant(String participantId) {
        this.participants.removeIf(participant -> participant.getUserId().equals(participantId));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof final Giveaway giveaway) {
            return this.name.equals(giveaway.getName()) &&
                    this.description.equals(giveaway.getDescription()) &&
                    this.pacifistaCommandToSend.equals(giveaway.getPacifistaCommandToSend()) &&
                    this.winners == giveaway.getWinners() &&
                    this.participants.equals(giveaway.getParticipants()) &&
                    super.equals(obj);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description, pacifistaCommandToSend, winners, participants);
    }

    @Override
    public String toString() {
        return "Giveaway{" +
                "giveawayId=" + super.getId() +
                ", createdAt=" + super.getCreatedAt() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pacifistaCommandToSend='" + pacifistaCommandToSend + '\'' +
                ", winners=" + winners +
                ", participants=" + participants +
                '}';
    }
}
