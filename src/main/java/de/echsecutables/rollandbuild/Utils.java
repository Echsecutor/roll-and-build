package de.echsecutables.rollandbuild;

import de.echsecutables.rollandbuild.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

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

    public static Board boardFromConfig(GameConfig gameConfig) {
        Board board = new Board();
        if (gameConfig == null) {
            return board;
        }

        board.setShape(new Shape(gameConfig.getBoardWidth(), gameConfig.getBoardHeight()));
        board.setCounters(gameConfig.getInitialCounters().clone());
        board.setAvailableBuildings(Utils.numberOfBuildingsToBuildingList(gameConfig.getInitialBuildings()));

        return board;
    }

    public static List<Building> numberOfBuildingsToBuildingList(List<Pair<Integer, BuildingType>> numberOfBuildings) {
        List<Building> re = new ArrayList<>();
        for (Pair<Integer, BuildingType> typePair : numberOfBuildings) {
            for (int i = 0; i < typePair.getFirst(); i++) {
                re.add(new Building(typePair.getSecond()));
            }
        }
        return re;
    }
}
