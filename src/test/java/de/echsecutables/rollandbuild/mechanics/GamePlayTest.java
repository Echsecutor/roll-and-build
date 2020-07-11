package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.models.Dice;
import de.echsecutables.rollandbuild.models.DiceFace;
import de.echsecutables.rollandbuild.models.DiceSymbol;
import de.echsecutables.rollandbuild.models.FaceCombinationType;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.ArrayList;

class GamePlayTest {

    @Test
    void roll() {
        DiceFace disaster = new DiceFace();
        disaster.setFaceCombinationType(FaceCombinationType.SYMBOL);
        disaster.setDiceSymbol(DiceSymbol.DISASTER);

        DiceFace crop = new DiceFace();
        crop.setFaceCombinationType(FaceCombinationType.SYMBOL);
        crop.setDiceSymbol(DiceSymbol.CROP);

        Dice dice = new Dice();
        ArrayList<Pair<Integer, DiceFace>> numberOfSidesWithFaces = new ArrayList<>();

        numberOfSidesWithFaces.add(Pair.of(1, disaster));
        numberOfSidesWithFaces.add(Pair.of(1, crop));

        dice.setNumberOfSidesWithFaces(numberOfSidesWithFaces);

        long seed = 42;

        boolean rolledDisaster = false;
        boolean rolledCrop = false;

        for (int i = 0; i < 5; i++) {
            seed *= 42;
            DiceFace rolled = GamePlay.roll(dice, seed);
            if (rolled == crop) {
                rolledCrop = true;
            } else if (rolled == disaster) {
                rolledDisaster = true;
            } else {
                Assert.fail("None of the two sides rolled.");
            }
        }
        Assert.assertTrue(rolledCrop);
        Assert.assertTrue(rolledDisaster);
    }

}