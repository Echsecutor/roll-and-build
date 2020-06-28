package de.echsecutables.rollandbuild;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.echsecutables.rollandbuild.models.Orientation;
import de.echsecutables.rollandbuild.models.Shape;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class UtilsTest {

    @Test
    void rotateShape() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String tShapeJson = Utils.resourceFileContentToString(getClass(), "tShape.json");
        String cwExpectedJSON = Utils.resourceFileContentToString(getClass(), "tShapeCW.json");
        String ccwExpectedJSON = Utils.resourceFileContentToString(getClass(), "tShapeCCW.json");
        String halfExpectedJSON = Utils.resourceFileContentToString(getClass(), "tShapeHalf.json");

        Shape t = mapper.readValue(tShapeJson, Shape.class);

        Shape cw = Utils.rotateShape(t, Orientation.CLOCKWISE);
        Shape cwExpected = mapper.readValue(cwExpectedJSON, Shape.class);
        Assert.assertEquals(cwExpected, cw);

        Shape ccw = Utils.rotateShape(t, Orientation.COUNTERCLOCKWISE);
        Shape ccwExpected = mapper.readValue(ccwExpectedJSON, Shape.class);
        Assert.assertEquals(ccwExpected, ccw);

        Shape half = Utils.rotateShape(t, Orientation.HALF);
        Shape halfExpected = mapper.readValue(halfExpectedJSON, Shape.class);
        Assert.assertEquals(halfExpected, half);

        //System.out.println(mapper.writeValueAsString(half));

    }
}