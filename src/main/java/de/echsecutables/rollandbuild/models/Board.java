package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Data
@NoArgsConstructor
@ApiModel(description = "A concrete Board used by a player in a game to place buildings.")
@Embeddable
public class Board {

    @ApiModelProperty(value = "width/height")
    private Point size;



}
