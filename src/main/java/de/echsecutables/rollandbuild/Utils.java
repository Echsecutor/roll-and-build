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

    private Utils(){}

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

}
