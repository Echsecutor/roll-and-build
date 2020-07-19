package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Represents a number of DiceFaces.")
// Pair<Integer, DiceFace> with nicer JSON
public class NumberOfDiceFaces {

    private int number;

    private DiceFace diceFace;
}
