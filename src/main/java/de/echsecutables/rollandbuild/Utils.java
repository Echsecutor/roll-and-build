package de.echsecutables.rollandbuild;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.echsecutables.rollandbuild.controllers.exceptions.BadRequestException;
import de.echsecutables.rollandbuild.controllers.exceptions.BugFoundException;
import de.echsecutables.rollandbuild.models.Building;
import de.echsecutables.rollandbuild.models.NumberOfBuildingType;
import de.echsecutables.rollandbuild.persistence.LongId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.repository.CrudRepository;

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

    public static <T extends LongId> T findAndChangeOrCreateNew(String tJson, CrudRepository<T, Long> repository, Class<T> typeParameterClass) {
        if (tJson != null && !tJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                T input = objectMapper.readValue(tJson, typeParameterClass);
                if (input.getId() != null && repository.findById(input.getId()).isEmpty()) { // given id does not exist -> autoincrement new id instead of given one
                    input.setId(null);
                }
                try {
                    return repository.save(input);
                } catch (InvalidDataAccessApiUsageException ex) {
                    LOGGER.debug(ex.toString());
                    throw new BadRequestException("Objects with their own ID can not be changed through containers. E.g. Change a Dice directly not through a containing BuildingType.");
                }
            } catch (JsonProcessingException e) {
                LOGGER.debug(e.toString());
            }
        }

        LOGGER.debug("'{}' is not a valid Entity -> creating empty one", tJson);
        try {
            return repository.save(typeParameterClass.getDeclaredConstructor().newInstance());
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
            throw new BugFoundException("Could not call constructor for type " + typeParameterClass.getName());
        }
    }
}
