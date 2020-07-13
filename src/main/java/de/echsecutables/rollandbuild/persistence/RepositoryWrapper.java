package de.echsecutables.rollandbuild.persistence;

import de.echsecutables.rollandbuild.models.Building;
import de.echsecutables.rollandbuild.models.BuildingType;
import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;

import java.util.Optional;

public interface RepositoryWrapper {

    Player getOrCreatePlayer(String sessionId);
    Player save(Player player);

    Game save(Game game);
    Optional<Game> loadGame(long gameId);

    Optional<BuildingType> loadBuilding(long id);
    BuildingType save(BuildingType building);

}
