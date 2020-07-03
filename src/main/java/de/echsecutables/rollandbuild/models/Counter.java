package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Resource Counters present on the player boards")
public enum Counter {
    CROP(DiceSymbol.CROP, 0), // Increase stored food
    WOOD(DiceSymbol.WOOD, 1), // Increase amount of stored building material: Wood
    STONE(DiceSymbol.STONE, 2), // Increase amount of stored building material: Stone
    GOLD(DiceSymbol.GOLD, 3), // Increase amount of stored building material: GOld
    CULTURE(DiceSymbol.CULTURE, 4), // Increase Culture (= victory points)
    DISASTER(DiceSymbol.DISASTER, 5); // Something bad will happen in the disaster resolution phase. A dice showing a disaster symbol must not be re-rolled.

    private final DiceSymbol diceSymbol;
    private final int index;

    Counter(DiceSymbol diceSymbol, int index) {
        this.diceSymbol = diceSymbol;
        this.index = index;
    }

    public DiceSymbol toDiceSymbol() {
        return diceSymbol;
    }

    public int index() {
        return index;
    }
}
