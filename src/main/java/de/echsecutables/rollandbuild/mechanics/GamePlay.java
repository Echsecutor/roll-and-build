package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.models.Dice;
import de.echsecutables.rollandbuild.models.DiceFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        int totalNumFaces = dice.getDiceFaces().size();
        Random r = new Random();
        if (seed != null) {
            r.setSeed(seed);
        }
        int rolled = r.nextInt(totalNumFaces);
        LOGGER.debug("rolled / total = {} / {}", rolled, totalNumFaces);
        return dice.getDiceFaces().get(rolled);
    }
}
