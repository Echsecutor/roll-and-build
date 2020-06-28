package de.echsecutables.rollandbuild.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Data
@NoArgsConstructor
@Embeddable
@ApiModel(description = "A concrete building placed on a players board in a concrete game.")
public class Building {

    @ApiModelProperty(value = "Most of the information (shape, dice, etc) are encoded in the building type.")
    private BuildingType buildingType;

    @ApiModelProperty(value = "left/down shift from top left corner of board to top left corner of rotated shape.")
    private Point position;

    @ApiModelProperty(value = "Rotation of the shape defined in the buildingType")
    private Orientation orientation;


}
