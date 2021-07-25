package de.echsecutables.rollandbuild.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.echsecutables.rollandbuild.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
        // also covers Geometry.insert
    void addBuilding() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = Utils.resourceFileContentToString(getClass(), "tShape.json");
        Shape t = mapper.readValue(json, Shape.class);
        Building tHut1 = new Building(new BuildingType(t));

        int width = 10;
        int height = 10;
        Board board = new Board(width, height);

        // initially empty
        assertEquals((new Shape(width, height)), board.getShape());


        boolean added = board.placeBuilding(tHut1);
        // not available
        assertFalse(added);

        board.getAvailableBuildings().add(tHut1);
        added = board.placeBuilding(tHut1);
        assertTrue(added);
        assertEquals(1, board.getPlacedBuildings().size());

        json = Utils.resourceFileContentToString(getClass(), "tShapein10x10.json");
        Shape t10x10 = mapper.readValue(json, Shape.class);

        assertEquals(t10x10, board.getShape());

        board.getAvailableBuildings().add(tHut1);
        added = board.placeBuilding(tHut1);
        // does not fit
        assertFalse(added);
        assertEquals(1, board.getPlacedBuildings().size());

        Building tHut2 = new Building(new BuildingType(t));
        tHut2.setPosition(new Point(1, 1));

        board.getAvailableBuildings().add(tHut2);
        added = board.placeBuilding(tHut2);
        // does not fit
        assertFalse(added);
        assertEquals(1, board.getPlacedBuildings().size());


        tHut2.setPosition(new Point(2, 1));
        added = board.placeBuilding(tHut2);
        assertTrue(added);
        assertEquals(2, board.getPlacedBuildings().size());
        assertTrue(board.getPlacedBuildings().contains(tHut1));
        assertTrue(board.getPlacedBuildings().contains(tHut2));

        json = Utils.resourceFileContentToString(getClass(), "twots.json");
        Shape tt = mapper.readValue(json, Shape.class);
        assertEquals(tt, board.getShape());
    }
}