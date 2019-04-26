package bio.singa.mathematics.geometry.edges;

import bio.singa.mathematics.vectors.Vector2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class LineSegmentTest {

    @Test
    void toUnitVector() {
        LineSegment lineSegment = new SimpleLineSegment(-1,4,2,3);
        Vector2D expected = new Vector2D(0.9486832980505138, -0.31622776601683794);
        assertEquals(expected, lineSegment.getUnitVector());
    }



}