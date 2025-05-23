package fr.pacifista.bot.core.files.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.pacifista.bot.core.exceptions.PacifistaBotException;
import fr.pacifista.bot.core.exceptions.UserBotException;
import fr.pacifista.bot.core.files.entities.FileGsonData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class FilesGsonService<T extends FileGsonData> {

    private final Gson gson;
    private final File directory;
    private final String fileName;

    @Getter
    private final Collection<T> entities;

    protected FilesGsonService(final String directoryName, final String fileName) throws PacifistaBotException {
        try {
            this.directory = new File(directoryName);
            this.fileName = fileName;
            this.gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            if (!this.directory.exists() && !this.directory.mkdirs()) {
                throw new Exception("The " + directoryName + " folder can't be created.");
            }

            this.entities = this.loadEntities();
        } catch (Exception e) {
            throw new PacifistaBotException("Impossible de créer le dossier " + directoryName + ".", e);
        }
    }

    public void onCreate(final @NonNull T entity) throws PacifistaBotException, UserBotException {
    }

    public final T create(final @NonNull T request) throws PacifistaBotException, UserBotException {
        final File file = new File(directory, this.generateFileName(request));

        if (file.exists() || this.getById(request.getId()) != null) {
            throw new UserBotException("Le " + fileName + " existe déjà.");
        }

        try {
            final String json = this.gson.toJson(request);

            if (!file.createNewFile()) {
                throw new Exception("Impossible de créer le fichier " + fileName + ".");
            }

            try {
                Files.write(file.toPath(), json.getBytes(), StandardOpenOption.WRITE);
                this.entities.add(request);
                this.onCreate(request);
                return request;
            } catch (Exception e) {
                file.delete();
                throw e;
            }
        } catch (Exception e) {
            throw new PacifistaBotException("Impossible de créer le " + fileName + ".", e);
        }
    }

    public void onDelete(final @NonNull T entity) throws PacifistaBotException, UserBotException {
    }

    public final void delete(final UUID id) throws PacifistaBotException, UserBotException {
        final T entity = this.getById(id);

        if (entity == null) {
            throw new UserBotException("Le " + fileName + " n'existe pas.");
        } else {
            this.delete(entity);
        }
    }

    public final void delete(final @NonNull T entity) throws PacifistaBotException, UserBotException {
        final File file = this.getFile(entity);

        if (file == null) {
            throw new UserBotException("Le " + fileName + " n'existe pas.");
        }

        try {
            if (!file.delete()) {
                throw new Exception("Impossible de supprimer le fichier " + fileName + ".");
            }

            this.entities.remove(entity);
            this.onDelete(entity);
        } catch (Exception e) {
            throw new PacifistaBotException("Impossible de supprimer le " + fileName + ".", e);
        }
    }

    @Nullable
    public final T getById(final UUID id) {
        for (T entity : this.entities) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }

        return null;
    }

    private Collection<T> loadEntities() throws PacifistaBotException {
        final File[] files = this.directory.listFiles();
        if (files == null) {
            throw new PacifistaBotException("Impossible de charger les fichiers.");
        }

        final Collection<T> entities = new HashSet<>();
        final TypeToken<T> typeToken = new TypeToken<T>() {};
        String json;
        T entity;

        for (final File file : files) {
            try {
                json = Files.readString(file.toPath());
                entity = this.gson.fromJson(json, typeToken);
                entities.add(entity);
            } catch (Exception e) {
                throw new PacifistaBotException("Impossible de charger le fichier " + file.getName() + ".", e);
            }
        }

        return entities;
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public final void saveCacheToFiles() {
        File file;
        String json;

        try {
            for (T entity : this.entities) {
                file = this.getFile(entity);

                if (file != null) {
                    json = this.gson.toJson(entity);
                    Files.writeString(file.toPath(), json, StandardOpenOption.TRUNCATE_EXISTING);
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde du cache.", e);
        }
    }

    @Nullable
    private File getFile(final T entity) {
        final File file = new File(directory, this.generateFileName(entity));

        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    private String generateFileName(final T entity) {
        return String.format(
                "%s-%s.json",
                fileName,
                entity.getId()
        );
    }
}
