package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@ApiModel(description = "Represents a type of building.")
public class BuildingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Primary Key", example = "42")
    private Long id;

    @ApiModelProperty(value = "2d Shape of this buildings base")

    private Shape shape;

    @ApiModelProperty(value = "The dice to roll for this building or null if the building does not yield a dice.")
    @ManyToOne
    private Dice dice;

}
