package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "All types of symbols that may appear on dices. Rolling each symbol has a different effect.")
public enum DiceSymbol {
    CROP, // Increase stored food
    WOOD, // Increase amount of stored building material: Wood
    STONE, // Increase amount of stored building material: Stone
    GOLD, // Increase amount of stored building material: GOld
    CULTURE, // Increase Culture (= victory points)
    DISASTER, // Something bad will happen in the disaster resolution phase. A dice showing a disaster symbol must not be re-rolled.
    HAMMER, // One building per rolled hammer can be build during building phase
    SHIELD, // Protects from being attacked with swords
    SWORD, // During conflict resolution, the player can steal 1 resource from each other player that has rolled less combined shields and swords then this player has swords
    FEATHER // Allows to re-roll this dice together with another dice. This does not count to the 3 rolls total.

}
