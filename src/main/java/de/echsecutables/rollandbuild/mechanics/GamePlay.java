package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.controllers.exceptions.BugFoundException;
import de.echsecutables.rollandbuild.models.Dice;
import de.echsecutables.rollandbuild.models.DiceFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.util.Random;

// hard coded rules
public class GamePlay {
    private static final Logger LOGGER = LoggerFactory.getLogger(GamePlay.class);

    private GamePlay() {
    }

    public static DiceFace roll(Dice dice) {
        return roll(dice, null);
    }

    // actual rolling
    public static DiceFace roll(Dice dice, Long seed) {

        LOGGER.debug("Rolling {} with seed {}", dice, seed);
        if (dice == null)
            return null;

        int totalNumFaces = 0;
        for (Pair<Integer, DiceFace> facePair : dice.getNumberOfSidesWithFaces()) {
            totalNumFaces += facePair.getFirst();
        }
        Random r = new Random();
        if (seed != null) {
            r.setSeed(seed);
        }
        int rolled = r.nextInt(totalNumFaces) + 1;
        LOGGER.debug("rand / total = {} / {}", rolled, totalNumFaces);
        for (Pair<Integer, DiceFace> facePair : dice.getNumberOfSidesWithFaces()) {
            rolled -= facePair.getFirst();
            if (rolled <= 0) {
                LOGGER.debug("rolled: {}", facePair.getSecond());
                return facePair.getSecond();
            }
        }
        throw new BugFoundException("Error in rolling, this must be unreachable.");
    }
}
