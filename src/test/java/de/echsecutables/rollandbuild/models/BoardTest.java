package de.echsecutables.rollandbuild.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.echsecutables.rollandbuild.Utils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

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
        Assert.assertEquals((new Shape(width, height)), board.getShape());



        boolean added = board.placeBuilding(tHut1);
        // not available
        Assert.assertFalse(added);

        board.getAvailableBuildings().add(tHut1);
        added = board.placeBuilding(tHut1);
        Assert.assertTrue(added);
        Assert.assertEquals(1, board.getPlacedBuildings().size());

        json = Utils.resourceFileContentToString(getClass(), "tShapein10x10.json");
        Shape t10x10 = mapper.readValue(json, Shape.class);

        Assert.assertEquals(t10x10, board.getShape());

        board.getAvailableBuildings().add(tHut1);
        added = board.placeBuilding(tHut1);
        // does not fit
        Assert.assertFalse(added);
        Assert.assertEquals(1, board.getPlacedBuildings().size());

        Building tHut2 = new Building(new BuildingType(t));
        tHut2.setPosition(new Point(1, 1));

        board.getAvailableBuildings().add(tHut2);
        added = board.placeBuilding(tHut2);
        // does not fit
        Assert.assertFalse(added);
        Assert.assertEquals(1, board.getPlacedBuildings().size());


        tHut2.setPosition(new Point(2, 1));
        added = board.placeBuilding(tHut2);
        Assert.assertTrue(added);
        Assert.assertEquals(2, board.getPlacedBuildings().size());
        Assert.assertTrue(board.getPlacedBuildings().contains(tHut1));
        Assert.assertTrue(board.getPlacedBuildings().contains(tHut2));

        json = Utils.resourceFileContentToString(getClass(), "twots.json");
        Shape tt = mapper.readValue(json, Shape.class);
        Assert.assertEquals(tt, board.getShape());
    }
}