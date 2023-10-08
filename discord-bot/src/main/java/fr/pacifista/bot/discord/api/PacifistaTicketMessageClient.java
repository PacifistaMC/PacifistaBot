package fr.pacifista.bot.discord.api;

import fr.pacifista.api.support.tickets.client.dtos.PacifistaSupportTicketMessageDTO;
import fr.pacifista.bot.discord.config.Config;

public class PacifistaTicketMessageClient extends PacifistaApiClient<PacifistaSupportTicketMessageDTO> {
    public PacifistaTicketMessageClient(Config config) {
        super(config, "/support/ticket/message");
    }

    @Override
    protected Class<PacifistaSupportTicketMessageDTO> getDtoClass() {
        return PacifistaSupportTicketMessageDTO.class;
    }
}
