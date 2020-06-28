package de.echsecutables.rollandbuild.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Transient;


@Data
@NoArgsConstructor
@ApiModel(description = "A 2d grid of occupied/unoccupied fields representing the shape of a buildings base.")
@Embeddable
public class Shape {

    @ApiModelProperty(value = "A matrix representing which fields are occupied by the shape. By convention, all columns have the same height.")
    private boolean[][] occupied;

    private void assertShapeInitialised() {
        if (occupied == null || occupied.length <= 0 || occupied[0].length <= 0) {
            throw new IllegalStateException("Shape not initialised before access!");
        }
    }

    private void assertCoordinatesInRange(int x, int y) throws IllegalArgumentException {
        assertShapeInitialised();
        if (!(0 <= x && x < getWidth() && 0 <= y && y < getHeight())) {
            throw new IllegalArgumentException("Invalid coordinates (" + x + ", " + y + ") for Shape [" + getWidth() + ", " + getHeight() + "]");
        }
    }

    public Shape(int width, int height) {
        occupied = new boolean[height][width];
    }

    @Transient
    @JsonIgnore
    public int getHeight() {
        assertShapeInitialised();
        return occupied.length;
    }

    @Transient
    @JsonIgnore
    public int getWidth() {
        assertShapeInitialised();
        return occupied[0].length;
    }

    @Transient
    public boolean getOccupied(int x, int y) throws IllegalArgumentException {
        assertCoordinatesInRange(x, y);
        return occupied[y][x];
    }

    public void setOccupied(int x, int y, boolean value) throws IllegalArgumentException {
        assertCoordinatesInRange(x, y);
        occupied[y][x] = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Shape:\n");
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                if (getOccupied(x, y)) {
                    builder.append("X");
                } else {
                    builder.append("O");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

}
