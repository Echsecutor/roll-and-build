package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class GamePlayRepositories {
    private static final Logger LOGGER = LoggerFactory.getLogger(GamePlayRepositories.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    public Player getOrCreatePlayer(String sessionId) {
        List<Player> players = playerRepository
                .findBySessionId(sessionId);
        if (!players.isEmpty()) {
            LOGGER.debug("Found record for player {}", players.get(0));
            if (players.size() > 1) {
                LOGGER.error("FIXME! More than one player for sessionId! {}", players);
            }
            return players.get(0);
        }
        Player player = playerRepository.save(new Player(sessionId));
        LOGGER.debug("Created new record for player {}", player);
        if (player.getId() == null) {
            LOGGER.error("FIXME! Player ID must be set when saving! {}", player);
        }
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
