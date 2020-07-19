package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.models.Dice;
import de.echsecutables.rollandbuild.models.DiceFace;
import de.echsecutables.rollandbuild.models.DiceSymbol;
import de.echsecutables.rollandbuild.models.NumberOfDiceFaces;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class GamePlayTest {

    @Test
    void roll2Sided() {

        DiceFace disaster = new DiceFace(List.of(DiceSymbol.DISASTER));
        DiceFace crop = new DiceFace(List.of(DiceSymbol.CROP));

        Dice dice = new Dice();
        dice.addSides(1, disaster);
        dice.addSides(1, crop);

        checkAllRolled(dice);
    }

    private void checkAllRolled(Dice dice) {
        List<DiceFace> diceFaces = dice.getNumberOfSidesWithFaces()
                .stream()
                .map(NumberOfDiceFaces::getDiceFace)
                .collect(Collectors.toList());

        Assert.assertTrue(diceFaces.size() > 0);

        boolean[] rolled = new boolean[diceFaces.size()];

        long seed = 23;
        int num_sides = dice.getNumberOfSidesWithFaces()
                .stream()
                .mapToInt(NumberOfDiceFaces::getNumber)
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
            Assert.assertTrue("Not all faces where rolled!", faceRolled);
        }
    }

    // also tests dice factory to create some complex faces
    @Test
    void complexDiceRoll() {
        DiceFace disaster = new DiceFace(List.of(DiceSymbol.CROP, DiceSymbol.DISASTER, DiceSymbol.HAMMER));
        DiceFace choice = new DiceFace(List.of(DiceSymbol.CROP, DiceSymbol.CROP), List.of(DiceSymbol.HAMMER, DiceSymbol.HAMMER));
        DiceFace crop = new DiceFace(List.of(DiceSymbol.CROP, DiceSymbol.CROP, DiceSymbol.CROP));

        Dice dice = new Dice();
        dice.addSides(1, disaster);
        dice.addSides(2, choice);
        dice.addSides(3, crop);

        checkAllRolled(dice);

    }

}