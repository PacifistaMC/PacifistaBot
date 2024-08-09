package fr.pacifista.bot.discord.tasks;

import fr.pacifista.api.server.essentials.client.status.clients.PacifistaStatusClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "UpdatePlayerCountTasks")
@RequiredArgsConstructor
@Service
public class UpdatePlayerCountTasks {

    private final JDA jda;
    private final PacifistaStatusClient pacifistaStatusClient;

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void updatePlayerCount() {
        final Integer playerCount = getPlayerCount();

        final Presence presence = jda.getPresence();
        if (playerCount == null) {
            presence.setActivity(Activity.watching("Pacifista hors ligne"));
        } else {
            presence.setActivity(Activity.watching(playerCount + " joueurs"));
        }
    }

    @Nullable
    private Integer getPlayerCount() {
        try {
            return pacifistaStatusClient.getServerInfo().getOnlinePlayers();
        } catch (Exception e) {
            log.error("Impossible de récupérer le nombre de joueurs connectés", e);
            return null;
        }
    }
}
