package bio.singa.simulation.model.agents.volumelike;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.topology.grids.rectangular.MooreRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * @author cl
 */
public class VolumeLikeAgentFactory {

    private VolumeLikeAgentFactory() {

    }

    public static ActinCortex createActinCortex(Membrane cellMembrane, MooreRectangularDirection innerDirection, Quantity<Length> width, Quantity<Length> distance) {
        ArrayDeque<Vector2D> vertices = new ArrayDeque<>();
        Iterator<MembraneSegment> segmentIterator = cellMembrane.getSegments().iterator();
        double distanceSimulation = Environment.convertSystemToSimulationScale(distance);
        double widthSimulation = Environment.convertSystemToSimulationScale(width);
        while (segmentIterator.hasNext()) {
            MembraneSegment segment = segmentIterator.next();
            Vector2D closePoint = segment.getStartingPoint().add(new Vector2D(innerDirection.getDirectionX() * distanceSimulation, innerDirection.getDirectionY() * distanceSimulation));
            vertices.addFirst(closePoint);
            Vector2D farPoint = closePoint.add(new Vector2D(innerDirection.getDirectionX() * widthSimulation, innerDirection.getDirectionY() * widthSimulation));
            vertices.addLast(farPoint);
            if (!segmentIterator.hasNext()) {
                Vector2D closeEndPoint = segment.getEndingPoint().add(new Vector2D(innerDirection.getDirectionX() * distanceSimulation, innerDirection.getDirectionY() * distanceSimulation));
                vertices.addFirst(closeEndPoint);
                Vector2D farEndPoint = closeEndPoint.add(new Vector2D(innerDirection.getDirectionX() * widthSimulation, innerDirection.getDirectionY() * widthSimulation));
                vertices.addLast(farEndPoint);
            }
        }
        VertexPolygon volumePolygon = new VertexPolygon(vertices, false);
        return new ActinCortex(volumePolygon);
    }


}
