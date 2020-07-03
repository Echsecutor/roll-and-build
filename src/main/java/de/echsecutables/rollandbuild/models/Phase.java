package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Phases of the game: determines which actions are currently allowed/whats happening")
public enum Phase {
    NOT_READY, // Game Config not loaded yet
    READY, // waiting for other players/all go!
    // Start of the actual Game
    SETUP, // initial state. Players setup their board by placing initial buildings
    ROLLING, // Start of each turn: players roll, keep, re roll - parallel and secretly
    GATHERING, // From here on, rolling results are public. Players get resources. - parallel - public - automatic (do we need an extra phase?)
    DISASTER, // Feeding People + Disaster resolution - one by one (disasters may affect others) - public
    BUILDING, // Players spend resources to place buildings - one by one (available buildings are limited) -public
    CONFLICT, // Strong players can rob resources from unprotected/weak players
    END_OF_TURN // Check game end conditions, advance turn starting player, wipe cookie crumbles...  goto ROLLING
}
