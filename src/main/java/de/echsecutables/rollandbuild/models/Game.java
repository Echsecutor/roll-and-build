package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Data
@AllArgsConstructor
@Entity
@ApiModel(value = "Game", description = "Represents a concrete Game instance")
public class Game {

    @Id
    @ApiModelProperty(value = "Primary Key", required = true, example = "42")
    private Long id;

    @ApiModelProperty(value = "IDs of players in this game.")
    private List<String> players;

}
