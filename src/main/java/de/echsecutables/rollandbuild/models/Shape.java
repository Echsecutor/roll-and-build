package de.echsecutables.rollandbuild.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;


@Data
@NoArgsConstructor
@ApiModel(description = "A 2d grid of occupied/unoccupied fields representing the shape of a buildings base.")
public class Shape {

    @ApiModelProperty(value = "Bounding box width")
    private int width = 0;

    @ApiModelProperty(value = "Bounding box height")
    private int height = 0;

    @ApiModelProperty(value = "A width x height matrix representing which fields are occupied by the shape.")
    private boolean[][] occupied;

    private void assertCoordinatesInRange(int x, int y) throws IllegalArgumentException {
        if (!(0 <= x && x < width && 0 <= y && y < height)) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
    }

    private void resetOccupied() {
        this.occupied = new boolean[this.width][this.height];
    }

    public void setWidth(int width) {
        this.width = width;
        resetOccupied();
    }

    public void setHeight(int height) {
        this.height = height;
        resetOccupied();
    }

    @Transient
    public boolean getOccupied(int x, int y) throws IllegalArgumentException {
        assertCoordinatesInRange(x, y);
        return occupied[x][y];
    }

    @Transient
    public void setOccupied(int x, int y, boolean value) throws IllegalArgumentException {
        assertCoordinatesInRange(x, y);
        occupied[x][y] = value;
    }
}
