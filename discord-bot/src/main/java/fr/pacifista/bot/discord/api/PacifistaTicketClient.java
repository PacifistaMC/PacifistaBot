package fr.pacifista.bot.discord.api;

import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketDTO;
import fr.pacifista.bot.discord.config.Config;

public class PacifistaTicketClient extends PacifistaApiClient<PacifistaSupportTicketDTO> {
    public PacifistaTicketClient(Config config) {
        super(config, "/support/ticket");
    }

    @Override
    protected Class<PacifistaSupportTicketDTO> getDtoClass() {
        return PacifistaSupportTicketDTO.class;
    }
}
