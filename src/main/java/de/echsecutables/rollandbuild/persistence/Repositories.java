package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class Repositories {
    private static final Logger LOGGER = LoggerFactory.getLogger(Repositories.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    public Player getOrCreatePlayer(String sessionId) {
        List<Player> players = playerRepository
                .findBySessionId(sessionId);
        if (!players.isEmpty()) {
            LOGGER.debug("Found record for player {}", players.get(0));
            return players.get(0);
        }
        Player player = playerRepository.save(new Player(sessionId));
        LOGGER.debug("Created new record for player {}", player);
        return player;
    }

    public Player save(Player player) {
        return playerRepository.save(player);
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }

    public Optional<Game> loadGame(long gameId) {
        return gameRepository.findById(gameId);
    }

}
