package fr.pacifista.bot.core.files.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public abstract class FileGsonData {

    @NonNull
    private UUID id;

    @NonNull
    private Date createdAt;

    public FileGsonData() {
        this.id = UUID.randomUUID();
        this.createdAt = new Date();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof final FileGsonData fileGsonData) {
            return this.id.equals(fileGsonData.getId()) &&
                    this.createdAt.equals(fileGsonData.getCreatedAt());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "FileGsonData{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                '}';
    }
}
