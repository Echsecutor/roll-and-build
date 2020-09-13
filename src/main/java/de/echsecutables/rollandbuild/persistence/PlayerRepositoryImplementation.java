package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PlayerRepositoryImplementation {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerRepositoryImplementation.class);

    @Autowired
    private PlayerRepository playerRepository;

    public <S extends Player> S save(S s) {
        return playerRepository.save(s);
    }

    public Player getOrCreatePlayer(String sessionId) {
        List<Player> players = playerRepository.findBySessionId(sessionId);
        if (players.isEmpty()) {
            return playerRepository.save(new Player(sessionId));
        }
        if (players.size() > 1) {
            LOGGER.error("FIXME! More than one player with session id {}", sessionId);
        }
        return players.get(0);
    }
}
