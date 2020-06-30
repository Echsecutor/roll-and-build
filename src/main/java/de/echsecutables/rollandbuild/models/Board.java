package de.echsecutables.rollandbuild.models;

import de.echsecutables.rollandbuild.Geometry;
import de.echsecutables.rollandbuild.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@ApiModel(description = "A concrete Board used by a player in a game to place buildings.")
@Embeddable
public class Board {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    @ApiModelProperty(value = "The board shape keeps track of which fields are occupied by buildings in addition to the total width and height.")
    private Shape shape;

    @ApiModelProperty(value = "Buildings placed on this board.")
    private List<Building> buildings = new ArrayList<>();

    @ManyToOne
    @ApiModelProperty(value = "A board always belongs to a player.")
    private Player owner;

    public Board(int width, int height) {
        shape = new Shape(width, height);
    }

    @Transient
    // returns success
    public boolean addBuilding(Building building) {
        LOGGER.debug("Adding building {} to board {}", building, this);
        Optional<Shape> combined = Geometry.insert(shape,
                Geometry.rotateShape(building.getBuildingType().getShape(), building.getOrientation()),
                building.getPosition().getX(),
                building.getPosition().getY()
        );
        if (combined.isEmpty()) {
            LOGGER.debug("Building does not fit.");
            return false;
        }
        this.shape = combined.get();
        this.buildings.add(building);
        return true;
    }
}
