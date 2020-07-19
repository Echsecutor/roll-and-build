package de.echsecutables.rollandbuild;

import de.echsecutables.rollandbuild.models.Building;
import de.echsecutables.rollandbuild.models.NumberOfBuildingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    public static void logRequest(Logger logger, HttpServletRequest request) {
        String body = "";
        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception ignored) {
        }
        logger.trace("[{}] {} : {}\n{}", request.getSession().getId(), request.getMethod(), request.getRequestURI(), body);
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

    public static List<Building> numberOfBuildingsToBuildingList(List<NumberOfBuildingType> numberOfBuildings) {
        List<Building> re = new ArrayList<>();
        for (NumberOfBuildingType typePair : numberOfBuildings) {
            for (int i = 0; i < typePair.getNumber(); i++) {
                re.add(new Building(typePair.getBuildingType()));
            }
        }
        return re;
    }
}
