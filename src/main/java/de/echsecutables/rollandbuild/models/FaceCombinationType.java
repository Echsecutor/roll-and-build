package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Dice faces can be combined into a new face by either granting all symbols from both sub-faces" +
        " or by granting the option of choosing one of the two sub faces." +
        " A primitive (non-combined) face shows just one symbol.")
public enum FaceCombinationType {
    SYMBOL, // This face only shows one symbol
    OR, // Choose one of the two sub faces
    AND // Counts as both sub faces
}
