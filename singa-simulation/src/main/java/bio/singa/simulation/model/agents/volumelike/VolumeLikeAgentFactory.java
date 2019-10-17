package bio.singa.simulation.model.agents.volumelike;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.faces.ComplexPolygon;
import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.topology.grids.rectangular.MooreRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.sections.CellRegions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class VolumeLikeAgentFactory {

    private static final Logger logger = LoggerFactory.getLogger(VolumeLikeAgentFactory.class);

    private VolumeLikeAgentFactory() {

    }

    public static VolumeLikeAgent createCellCortex(Membrane cellMembrane, MooreRectangularDirection innerDirection, Quantity<Length> width, Quantity<Length> distance) {
        logger.info("Initializing cell cortex.");
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
        return new VolumeLikeAgent(volumePolygon, CellRegions.CELL_CORTEX);
    }

    public static VolumeLikeAgent createCellCortex(Membrane cellMembrane, MooreRectangularDirection innerDirection, LineSegment borderSegment) {
        logger.info("Initializing cell cortex.");
        List<Vector2D> vertices = new ArrayList<>(cellMembrane.getRegionMap().values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList()));
        vertices.add(borderSegment.getStartingPoint());
        vertices.add(borderSegment.getEndingPoint());

        VertexPolygon volumePolygon = new VertexPolygon(vertices, false);
        return new VolumeLikeAgent(volumePolygon, CellRegions.CELL_CORTEX);
    }

    public static VolumeLikeAgent createCellCortex(Membrane cellMembrane, Vector2D centroid, Quantity<Length> width, Quantity<Length> distance) {
        logger.info("Initializing cell cortex.");
        ArrayDeque<Vector2D> vertices = new ArrayDeque<>();
        Iterator<MembraneSegment> segmentIterator = cellMembrane.getSegments().iterator();
        double distanceSimulation = Environment.convertSystemToSimulationScale(distance);
        double widthSimulation = Environment.convertSystemToSimulationScale(width);

        while (segmentIterator.hasNext()) {
            MembraneSegment segment = segmentIterator.next();
            Vector2D direction = centroid.subtract(segment.getStartingPoint()).normalize();
            Vector2D closePoint = segment.getStartingPoint().add(new Vector2D(direction.getX() * distanceSimulation, direction.getY() * distanceSimulation));
            vertices.addFirst(closePoint);
            Vector2D farPoint = closePoint.add(new Vector2D(direction.getX() * widthSimulation, direction.getY() * widthSimulation));
            vertices.addLast(farPoint);
            if (!segmentIterator.hasNext()) {
                Vector2D closeEndPoint = segment.getEndingPoint().add(new Vector2D(direction.getX() * distanceSimulation, direction.getY() * distanceSimulation));
                vertices.addFirst(closeEndPoint);
                Vector2D farEndPoint = closeEndPoint.add(new Vector2D(direction.getX() * widthSimulation, direction.getY() * widthSimulation));
                vertices.addLast(farEndPoint);
            }
        }
        ComplexPolygon polygon = new ComplexPolygon(new ArrayList<>(vertices));
        return new VolumeLikeAgent(polygon, CellRegions.CELL_CORTEX);
    }


}
