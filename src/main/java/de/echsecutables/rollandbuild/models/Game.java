package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@Entity
@ApiModel(description = "Represents a concrete Game instance")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Primary Key", required = true, example = "42")
    private Long id;

    @ApiModelProperty(value = "IDs of players in this game.", example = "[23, 42]")
    private ArrayList<Long> playerIds = new ArrayList<>();

    @ApiModelProperty(value = "Current Game phase.", example = "ROLLING")
    private Phase phase = Phase.SETUP;

    @ApiModelProperty(value = "ID of the currently active player. Applicable in some phases.", example = "42")
    private Long activePlayerId;

}
