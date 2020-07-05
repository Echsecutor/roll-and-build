package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;

import java.util.Optional;

public interface RepositoryWrapper {

    Player getOrCreatePlayer(String sessionId);

    Player save(Player player);

    Game save(Game game);

    Optional<Game> loadGame(long gameId);

}
