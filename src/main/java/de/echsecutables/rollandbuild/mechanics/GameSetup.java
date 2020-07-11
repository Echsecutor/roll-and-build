package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.models.Board;
import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.GameConfig;
import de.echsecutables.rollandbuild.models.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// logic related to game setup
public class GameSetup {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSetup.class);

    private GameSetup() {
    }

    public static Board boardFromConfig(GameConfig gameConfig) {
        Board board = new Board();
        if (gameConfig == null) {
            return board;
        }

        board.setShape(new Shape(gameConfig.getBoardWidth(), gameConfig.getBoardHeight()));
        board.setCounters(gameConfig.getInitialCounters().clone());
        board.setAvailableBuildings(Utils.numberOfBuildingsToBuildingList(gameConfig.getInitialBuildings()));

        return board;
    }

    // kick and re-join players to refresh boards with new config
    public static void loadGameConfig(Game game, GameConfig gameConfig) {
        game.setGameConfig(gameConfig);
        List<Board> boards = game.getBoards();
        game.setBoards(new ArrayList<>());
        boards.stream()
                .map(Board::getOwner)
                .forEach(game::join);
    }

}
