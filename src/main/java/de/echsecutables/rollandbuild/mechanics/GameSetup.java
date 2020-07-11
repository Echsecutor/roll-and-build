package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.models.Board;
import de.echsecutables.rollandbuild.models.GameConfig;
import de.echsecutables.rollandbuild.models.Shape;

public class GameSetup {
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


}
