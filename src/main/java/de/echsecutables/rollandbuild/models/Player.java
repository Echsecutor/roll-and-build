package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@ApiModel(description = "A user playing zero or many games.")
public class Player {
    private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Primary Key", example = "42")
    private Long id;

    @Column(unique = true)
    @ApiModelProperty(value = "The user's session ID", example = "14c5d7e2-b8df-40aa-a282-78757197ee7d")
    private String sessionId;

    @ApiModelProperty(value = "The user's Nickname to be displayed to others. Max 256 characters.", example = "Max Power")
    private String name;

    @ApiModelProperty(value = "IDs of games played by this user.")
    @ElementCollection
    private List<Long> games = new ArrayList<>();

    public Player(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", name='" + name + '\'' +
                ", games=" + games.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]")) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
