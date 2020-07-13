package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.models.Dice;
import de.echsecutables.rollandbuild.models.DiceFace;
import de.echsecutables.rollandbuild.models.DiceFaceFactory;
import de.echsecutables.rollandbuild.models.DiceSymbol;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.stream.Collectors;

class GamePlayTest {

    @Test
    void roll2Sided() {

        DiceFace disaster = DiceFaceFactory.singleSymbol(DiceSymbol.DISASTER);
        DiceFace crop = DiceFaceFactory.singleSymbol(DiceSymbol.CROP);

        Dice dice = new Dice();
        dice.addSides(1, disaster);
        dice.addSides(1, crop);

        checkAllRolled(dice);
    }

    private void checkAllRolled(Dice dice) {
        List<DiceFace> diceFaces = dice.getNumberOfSidesWithFaces()
                .stream()
                .map(Pair::getSecond)
                .collect(Collectors.toList());

        boolean[] rolled = new boolean[diceFaces.size()];

        long seed = 23;
        int num_sides = dice.getNumberOfSidesWithFaces()
                .stream()
                .mapToInt(Pair::getFirst)
                .sum();

        int num_rolls = 3 * num_sides; // do some more serious math if time permits ;)

        for (int j = 0; j < num_rolls; j++) {
            seed *= 42;
            DiceFace currentResult = GamePlay.roll(dice, seed);
            boolean found = false;
            for (int i = 0; i < diceFaces.size(); i++) {
                if (currentResult == diceFaces.get(i)) {
                    found = true;
                    rolled[i] = true;
                }
            }
            Assert.assertTrue("Rolled non existing face.", found);
        }
        for (boolean faceRolled : rolled) {
            Assert.assertTrue(faceRolled);
        }
    }

    // also tests dice factory
    @Test
    void complexDiceRoll() {
        DiceFace disaster = DiceFaceFactory.multiSymbol(List.of(DiceSymbol.CROP, DiceSymbol.DISASTER, DiceSymbol.HAMMER));
        DiceFace choice = DiceFaceFactory.or(
                DiceFaceFactory.multiSymbol(List.of(DiceSymbol.CROP, DiceSymbol.CROP)),
                DiceFaceFactory.multiSymbol(List.of(DiceSymbol.HAMMER, DiceSymbol.HAMMER))
        );
        DiceFace crop = DiceFaceFactory.multiSymbol(List.of(DiceSymbol.CROP, DiceSymbol.CROP, DiceSymbol.CROP));

        Dice dice = new Dice();
        dice.addSides(1, disaster);
        dice.addSides(2, choice);
        dice.addSides(3, crop);

        checkAllRolled(dice);

    }

}