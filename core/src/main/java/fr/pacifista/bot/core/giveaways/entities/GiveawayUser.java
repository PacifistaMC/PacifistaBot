package fr.pacifista.bot.core.giveaways.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Date;
import java.util.Objects;

@Getter
@AllArgsConstructor
public final class GiveawayUser {

    @NonNull
    private final String userId;

    @NonNull
    private final String userName;

    @NonNull
    private final Date createdAt;

    public GiveawayUser(@NonNull final String userId, @NonNull final String userName) {
        this.userId = userId;
        this.userName = userName;
        this.createdAt = new Date();
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, createdAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof final GiveawayUser participant) {
            return this.userId.equals(participant.getUserId()) &&
                    this.userName.equals(participant.getUserName()) &&
                    this.createdAt.equals(participant.getCreatedAt());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "GiveawayUser{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}
