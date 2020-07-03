package de.echsecutables.rollandbuild.mechanics;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.models.Orientation;
import de.echsecutables.rollandbuild.models.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

// Functional Methods for 2d discrete geometry
public class Geometry {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Geometry() {
    }

    // if innerShape fits into outerShape at position xShift (from left) and yShift (from top) return the combined shape
    // else empty
    // args unmodified
    public static Optional<Shape> insert(Shape outerShape, Shape innerShape, int xShift, int yShift) {
        LOGGER.debug("Inserting {} + ({}, {}) into {}", innerShape, xShift, yShift, outerShape);
        // check bounding box
        if (xShift < 0 || yShift < 0 || xShift + innerShape.getWidth() > outerShape.getWidth() || yShift + innerShape.getHeight() > outerShape.getHeight()) {
            LOGGER.debug("Bounding Box does not fit");
            return Optional.empty();
        }
        Shape combined = new Shape(outerShape.getWidth(), outerShape.getHeight());
        for (int x = 0; x < outerShape.getHeight(); x++) {
            for (int y = 0; y < outerShape.getHeight(); y++) {
                combined.setOccupied(x, y, outerShape.getOccupied(x, y));
                if (x >= xShift && x < xShift + innerShape.getWidth() &&
                        y > yShift && y < yShift + innerShape.getHeight() &&
                        innerShape.getOccupied(x - xShift, y - yShift)) {
                    if (combined.getOccupied(x, y)) {
                        LOGGER.debug("Collision at ({}, {})", x, y);
                        return Optional.empty();
                    }
                    combined.setOccupied(x, y, true);
                }
            }
        }
        LOGGER.debug("Combined Shape {}", combined);
        return Optional.of(combined);
    }


    // create a rotated copy of the Shape
    public static Shape rotateShape(Shape original, Orientation orientation) {
        LOGGER.debug("rotating {} to {}", original, orientation);

        if (orientation == Orientation.ORIGINAL)
            return original;

        Shape rotated;
        if (orientation == Orientation.HALF) {
            rotated = new Shape(original.getWidth(), original.getHeight());
            // mirror x and mirror y = 180 degree rotation
            for (int x = 0; x < rotated.getWidth(); x++) {
                for (int y = 0; y < rotated.getHeight(); y++) {
                    rotated.setOccupied(x, y, original.getOccupied(original.getWidth() - x - 1, original.getHeight() - y - 1));
                }
            }
        } else {
            rotated = new Shape(original.getHeight(), original.getWidth());
            for (int x = 0; x < rotated.getWidth(); x++) {
                for (int y = 0; y < rotated.getHeight(); y++) {
                    if (orientation == Orientation.CLOCKWISE) {
                        //noinspection SuspiciousNameCombination
                        rotated.setOccupied(x, y, original.getOccupied(y, original.getHeight() - x - 1));
                    } else {
                        //noinspection SuspiciousNameCombination
                        rotated.setOccupied(x, y, original.getOccupied(original.getWidth() - y - 1, x));
                    }
                }
            }
        }

        LOGGER.debug("rotated: {}", rotated);
        return rotated;
    }
}
