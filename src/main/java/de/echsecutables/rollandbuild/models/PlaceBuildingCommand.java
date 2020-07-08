package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(
        description = "Which buildingType should be placed where and in which orientation."
)
public class PlaceBuildingCommand {

    @ApiModelProperty(value = "The Building type must be identified by an existing building type Id")
    private Long buildingTypeId;

    @ApiModelProperty(value = "Placement Position")
    private Point point;

    @ApiModelProperty(value = "Rotation relative to buildingType shape")
    private Orientation orientation;
}
