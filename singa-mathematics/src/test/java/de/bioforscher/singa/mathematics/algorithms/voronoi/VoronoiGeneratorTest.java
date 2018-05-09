package de.bioforscher.singa.mathematics.algorithms.voronoi;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author cl
 */
public class VoronoiGeneratorTest {

    @Test
    @Ignore
    public void shouldGenerateLinearDiagram() {
        // currently not working
        ArrayList<Vector2D> points = new ArrayList<>();
        points.add(new Vector2D(50, 50));
        points.add(new Vector2D(100, 50));
        points.add(new Vector2D(150, 50));
        points.add(new Vector2D(200, 50));
        VoronoiGenerator.generateVoronoiDiagram(points, new Rectangle(250, 100));

    }


}