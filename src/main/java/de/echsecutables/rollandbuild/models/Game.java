package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToMany
    private List<Player> players = new ArrayList<>();

    @ApiModelProperty(value = "Current Game phase.", example = "ROLLING")
    private Phase phase = Phase.SETUP;

    @ApiModelProperty(value = "ID of the currently active player. Applicable in some phases.", example = "42")
    private Long activePlayerId;

}
