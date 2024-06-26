package fr.pacifista.bot.core.giveaways;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.pacifista.bot.core.giveaways.entities.Giveaway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j(topic = "Giveaways Manager")
@Service
public class GiveawaysManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File giveawaysFolder = new File("data" + File.separator + "giveaways");

    public GiveawaysManager() {
        try {
            if (!this.giveawaysFolder.exists() && !this.giveawaysFolder.mkdirs()) {
                throw new Exception("The data folder can't be created.");
            }
        } catch (Exception e) {
            log.error("Une erreur est survenue lors de la création de la classe GiveawayUtils.", e);
        }
    }

    public void createGiveaway(Giveaway giveaway) {
        try {
            String jsonGiveaway = gson.toJson(giveaway);
            String giveawayFilePath = this.generateFilePath(giveaway.getGiveawayId());
            File giveawayFile = new File(giveawayFilePath);
            if (!giveawayFile.createNewFile()) {
                throw new Exception(String.format("Impossible de créer le fichier du giveaway. Id: %s", giveaway.getGiveawayId()));
            }
            Files.write(giveawayFile.toPath(), jsonGiveaway.getBytes(), StandardOpenOption.WRITE);
        } catch (Exception e) {
            log.error("Impossible d'enregistrer le giveaway dans le fichier.", e);
        }
    }

    public List<Giveaway> getGiveaways() {
        List<Giveaway> giveawayList = new ArrayList<>();
        File[] files = this.giveawaysFolder.listFiles();

        if (files == null) return giveawayList;

        try {
            for (File file : files) {
                String jsonGiveaway = Files.readString(file.toPath());
                Giveaway giveaway = this.gson.fromJson(jsonGiveaway, Giveaway.class);
                giveawayList.add(giveaway);
            }
        } catch (IOException e) {
            log.error("Impossible de récupérer les giveaways.", e);
        }
        return giveawayList;
    }

    public Giveaway getGiveawayById(UUID giveawayId) {
        List<Giveaway> giveawayList = this.getGiveaways();

        try {
            for (Giveaway giveaway : giveawayList) {
                if (giveaway.getGiveawayId().equals(giveawayId)) return giveaway;
            }
        } catch (Exception e) {
            log.error("Impossible de récupérer le giveaway. Id: {}", giveawayId, e);
        }

        return null;
    }

    public void updateGiveaway(Giveaway giveaway) {
        this.deleteGiveaway(giveaway.getGiveawayId());
        this.createGiveaway(giveaway);
    }

    public void deleteGiveaway(UUID giveawayId) {
        try {
            String giveawayFilePath = this.generateFilePath(giveawayId);
            Files.deleteIfExists(Path.of(giveawayFilePath));
        } catch(Exception e) {
            log.error("Impossible de supprimer le giveaway. Id: {}", giveawayId, e);
        }
    }

    private String generateFilePath(UUID giveawayId) {
        return this.giveawaysFolder.getAbsolutePath() + File.separator + "giveaway-" + giveawayId + ".json";
    }
}
