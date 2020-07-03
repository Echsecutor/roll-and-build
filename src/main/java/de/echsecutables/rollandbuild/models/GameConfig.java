package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.util.Pair;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

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
            value = "How many buildings of which type can be build in total.",
            required = true
    )
    ArrayList<Pair<Integer, BuildingType>> availableBuildings = new ArrayList<>();

}
