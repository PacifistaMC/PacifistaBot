package fr.pacifista.bot.core.giveaways.services;

import fr.pacifista.bot.core.exceptions.PacifistaBotException;
import fr.pacifista.bot.core.files.services.FilesGsonService;
import fr.pacifista.bot.core.giveaways.entities.Giveaway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "Giveaways Manager")
public final class GiveawaysService extends FilesGsonService<Giveaway> {

    public GiveawaysService() throws PacifistaBotException {
        super("giveaways", "giveaway");
    }

}
