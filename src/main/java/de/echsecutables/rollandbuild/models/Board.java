package de.echsecutables.rollandbuild.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.mechanics.Geometry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@ApiModel(description = "A concrete Board used by a player in a game to place buildings.")
@Entity
public class Board {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Primary Key", required = true, example = "42")
    private Long id;

    @ApiModelProperty(value = "The board shape keeps track of which fields are occupied by buildings in addition to the total width and height.")
    private Shape shape;

    @ApiModelProperty(value = "Buildings placed on this board.")
    @ElementCollection
    private List<Building> placedBuildings = new ArrayList<>();

    private int[] counters = new int[Counter.values().length];

    @ManyToOne
    @ApiModelProperty(value = "A board always belongs to a player.")
    private Player owner;

    @ApiModelProperty(value = "Buildings bought and to be placed on this board. " +
            "If this List is non-empty, the next action of the owner is to place these buildings.")
    @ElementCollection
    private List<Building> availableBuildings = new ArrayList<>();


    public Board(int width, int height) {
        shape = new Shape(width, height);
    }


    @Transient
    @JsonIgnore
    // contract: availableBuildings.contains(building)
    // returns success
    public boolean placeBuilding(Building building) {
        LOGGER.debug("Adding building {} to board {}", building, this);
        if (!availableBuildings.contains(building)) {
            LOGGER.error("Building '{}' not available for board '{}'!", building, this);
            return false;
        }
        Optional<Shape> combined = Geometry.insert(shape,
                Geometry.rotateShape(building.getBuildingType().getShape(), building.getOrientation()),
                building.getPosition().getX(),
                building.getPosition().getY()
        );
        if (combined.isEmpty()) {
            LOGGER.debug("Building does not fit.");
            return false;
        }
        this.availableBuildings.remove(building);
        this.shape = combined.get();
        this.placedBuildings.add(building);
        LOGGER.debug("Building {} placed.", building);

        return true;
    }

    @Transient
    @JsonIgnore
    public int getCounter(Counter counter) {
        return counters[counter.index()];
    }

    @Transient
    @JsonIgnore
    public void setCounter(Counter counter, int value) {
        counters[counter.index()] = value;
    }
}
