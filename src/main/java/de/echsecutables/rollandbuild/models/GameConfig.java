package de.echsecutables.rollandbuild.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.util.Pair;

import javax.persistence.*;
import java.util.ArrayList;

@Data
@Entity
@ApiModel(description = "Set of game specific rules.")
public class GameConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Primary Key", required = true, example = "42")
    private Long id;

    @ApiModelProperty(
            value = "Starting values for the players counters",
            required = true,
            example = "[4,0,0,0,0,0]"
    )
    private int[] initialCounters = new int[Counter.values().length];

    @ApiModelProperty(
            value = "How many buildings of which type can be build during the setup phase before the first round.",
            required = true
    )
    private ArrayList<Pair<Integer, BuildingType>> initialBuildings = new ArrayList<>();

    @ApiModelProperty(
            value = "How many buildings of which type can be build in total. Initial buildings are NOT subtracted.",
            required = true
    )
    private ArrayList<Pair<Integer, BuildingType>> availableBuildings = new ArrayList<>();


    @ApiModelProperty(
            value = "Width of players boards",
            required = true
    )
    private int boardWidth;
    @ApiModelProperty(
            value = "Height of players boards",
            required = true
    )
    private int boardHeight;

    @Transient
    @JsonIgnore
    public void addAvailableBuilding(int num, BuildingType buildingType) {
        this.availableBuildings.add(Pair.of(num, buildingType));
    }

    @Transient
    @JsonIgnore
    public void addInitialBuilding(int num, BuildingType buildingType) {
        this.initialBuildings.add(Pair.of(num, buildingType));
    }

}
