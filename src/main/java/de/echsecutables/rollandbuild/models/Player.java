package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@ApiModel(description = "A user playing zero or many games.")
public class Player {

    public Player(String sessionId) {
        this.sessionId = sessionId;
    }

    @Id
    @ApiModelProperty(value = "The user's session ID", required = true, example = "14c5d7e2-b8df-40aa-a282-78757197ee7d")
    private String sessionId;

    @ApiModelProperty(value = "The user's Nickname to be displayed to others. Max 256 characters.", example = "Max Power")
    private String name;

    @ApiModelProperty(value = "IDs of games played by this user.")
    private ArrayList<Long> games= new ArrayList<>();

}
