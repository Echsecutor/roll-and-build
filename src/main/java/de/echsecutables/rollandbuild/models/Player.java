package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@Entity
@ApiModel(description = "A user playing zero or many games.")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Primary Key", example = "42")
    private Long id;

    @Column(unique=true)
    @ApiModelProperty(value = "The user's session ID", example = "14c5d7e2-b8df-40aa-a282-78757197ee7d")
    private String sessionId;

    @ApiModelProperty(value = "The user's Nickname to be displayed to others. Max 256 characters.", example = "Max Power")
    private String name;

    @ApiModelProperty(value = "IDs of games played by this user.")
    private ArrayList<Long> gameIds = new ArrayList<>();


    public Player(String sessionId) {
        this.sessionId = sessionId;
    }

}
