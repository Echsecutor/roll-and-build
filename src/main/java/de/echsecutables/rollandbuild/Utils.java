package de.echsecutables.rollandbuild;

import de.echsecutables.rollandbuild.models.Orientation;
import de.echsecutables.rollandbuild.models.Shape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static void logRequest(Logger logger, HttpServletRequest request) {
        logger.trace("[" + request.getSession().getId() + "] " + request.getMethod() + ": " + request.getRequestURI());
    }

    // to avoid import org.apache.commons.io.IOUtils;
    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static <T> String resourceFileContentToString(Class<T> tClass, String fileName) throws IOException {
        try (InputStream stream = tClass.getClassLoader().getResourceAsStream(fileName)) {
            return Utils.convertStreamToString(stream);
        }
    }

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
                        rotated.setOccupied(x, y, original.getOccupied(y, original.getHeight() - x - 1));
                    } else {
                        rotated.setOccupied(x, y, original.getOccupied(original.getWidth() - y - 1, x));
                    }
                }
            }
        }

        LOGGER.debug("rotated: {}", rotated);
        return rotated;
    }
}
