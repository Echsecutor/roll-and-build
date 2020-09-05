package de.echsecutables.rollandbuild.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.echsecutables.rollandbuild.mechanics.GameSetup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@ApiModel(description = "Represents a concrete Game instance")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Primary Key", required = true, example = "42")
    private Long id;

    @ApiModelProperty(value = "Current Game phase.", example = "ROLLING")
    private Phase phase = Phase.NOT_READY;

    @ApiModelProperty(value = "IDs of players who ready to advance to the next phase.", example = "[23, 42]")
    @ElementCollection
    private List<Long> playersReady = new ArrayList<>();

    @ApiModelProperty(value = "The currently active player. Applicable in some phases.")
    @ManyToOne
    private Player activePlayer;

    @ApiModelProperty(value = "The players' boards in this game.")
    @ElementCollection
    private List<Board> boards = new ArrayList<>();

    @ApiModelProperty(value = "The game configuration holds some details of the rules which can be changed per game.")
    @ManyToOne
    private GameConfig gameConfig = null;

    @Transient
    @JsonIgnore
    public void join(Player player) {
        Board board = GameSetup.boardFromConfig(gameConfig);
        board.setOwner(player);
        this.boards.add(board);
    }

    @Transient
    @JsonIgnore
    // This ensures the Contract: for (Board board:boards){ assert (players.contains(board.getOwner())); }
    // + DRY
    public List<Player> getPlayers() {
        return this.boards.stream().map(Board::getOwner).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return id.equals(game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
