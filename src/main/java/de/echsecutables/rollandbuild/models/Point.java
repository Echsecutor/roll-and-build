package de.echsecutables.rollandbuild.models;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "2d discrete coordinates, i.e. Pair<Integer,Integer> + semantics")
@Embeddable
public class Point {

    @ApiModelProperty(value = "0 = left boarder", example = "0")
    private int x = 0;

    @ApiModelProperty(value = "0 = top boarder", example = "0")
    private int y = 0;
}
