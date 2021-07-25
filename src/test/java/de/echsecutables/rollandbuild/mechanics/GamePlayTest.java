package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.models.Dice;
import de.echsecutables.rollandbuild.models.DiceFace;
import de.echsecutables.rollandbuild.models.DiceSymbol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GamePlayTest {

    @Test
    void roll2Sided() {

        DiceFace disaster = new DiceFace(List.of(DiceSymbol.DISASTER));
        DiceFace crop = new DiceFace(List.of(DiceSymbol.CROP));

        Dice dice = new Dice();
        dice.getDiceFaces().add(disaster);
        dice.getDiceFaces().add(crop);

        checkAllRolled(dice);
    }

    private void checkAllRolled(Dice dice) {

        assertTrue(dice.getDiceFaces().size() > 0);

        boolean[] rolled = new boolean[dice.getDiceFaces().size()];

        long seed = 23;
        int num_sides = dice.getDiceFaces().size();

        int num_rolls = 3 * num_sides; // do some more serious math if time permits ;)

        for (int j = 0; j < num_rolls; j++) {
            seed *= 42;
            DiceFace currentResult = GamePlay.roll(dice, seed);
            boolean found = false;
            for (int i = 0; i < dice.getDiceFaces().size(); i++) {
                if (currentResult == dice.getDiceFaces().get(i)) {
                    found = true;
                    rolled[i] = true;
                }
            }
            assertTrue(found, "Rolled non existing face.");
        }
        for (boolean faceRolled : rolled) {
            assertTrue(faceRolled, "Not all faces where rolled!");
        }
    }

    // also tests dice factory to create some complex faces
    @Test
    void complexDiceRoll() {
        DiceFace disaster = new DiceFace(List.of(DiceSymbol.CROP, DiceSymbol.DISASTER, DiceSymbol.HAMMER));
        DiceFace choice = new DiceFace(List.of(DiceSymbol.CROP, DiceSymbol.CROP), List.of(DiceSymbol.HAMMER, DiceSymbol.HAMMER));
        DiceFace crop = new DiceFace(List.of(DiceSymbol.CROP, DiceSymbol.CROP, DiceSymbol.CROP));

        Dice dice = new Dice();
        dice.getDiceFaces().add(disaster);
        dice.getDiceFaces().add(choice);
        dice.getDiceFaces().add(crop);

        checkAllRolled(dice);

    }

}