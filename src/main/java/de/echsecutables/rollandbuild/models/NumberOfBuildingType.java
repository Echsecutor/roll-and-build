package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Represents a number of avaiable (not yet placed) buildings.")
// Pair<Integer, BuildingType> with nicer JSON
public class NumberOfBuildingType {

    @ApiModelProperty(value = "Number of available Buildings")
    private int number;

    @ApiModelProperty(value = "Type of Building")
    private BuildingType buildingType;
}
