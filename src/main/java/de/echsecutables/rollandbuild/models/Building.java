package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@Embeddable
@ApiModel(description = "A concrete building placed on a players board in a concrete game.")
public class Building {

    @ApiModelProperty(value = "Most of the information (shape, dice, etc) are encoded in the building type.", required = true)
    private BuildingType buildingType;

    @ApiModelProperty(value = "left/down shift from top left corner of board to top left corner of rotated shape.", required = true)
    private Point position = new Point(0, 0);

    @ApiModelProperty(value = "Rotation of the shape defined in the buildingType", required = true)
    private Orientation orientation = Orientation.ORIGINAL;

    @ApiModelProperty(value = "If the Building type has a dice, this field holds the currently rolled face.")
    private DiceFace lastRolled = null;

    public Building(BuildingType buildingType) {
        this.buildingType = buildingType;
    }
}
