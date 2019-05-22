package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Circles;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.*;

/**
 * @author cl
 */
public class MembraneBuilder {

    public static VectorStep linear() {
        return new LinearMembraneBuilder();
    }

    public interface VectorStep {

        InnerPointStep vectors(List<Vector2D> sortedVectors);

        InnerPointStep vectors(Vector2D... sortedVectors);

    }

    public interface InnerPointStep {

        GraphStep innerPoint(Vector2D vector2D);

    }

    public static ClosedVectorStep closed() {
        return new ClosedMembraneBuilder();
    }

    public interface ClosedVectorStep {

        GraphStep vectors(List<Vector2D> sortedVectors);

        GraphStep circle(Circle circle, int numberOfPoints);

        GraphStep polygon(Polygon polygon);

    }

    public interface GraphStep {

        MembraneRegionStep graph(AutomatonGraph automatonGraph);

    }

    public interface MembraneRegionStep {

        FinalStep membraneRegion(CellRegion innerRegion, CellRegion membraneRegion);

    }

    public interface RegionMappingStep extends MembraneRegionStep {

        MembraneRegionStep regionMapping(Map<Vector2D, CellRegion> regionMap);

    }

    public interface FinalStep {

        Membrane build();

    }

    public static abstract class GeneralMembraneBuilder implements GraphStep, RegionMappingStep, FinalStep {

        protected AutomatonGraph graph;
        protected CellRegion innerRegion;
        protected CellRegion membraneRegion;
        protected Map<Vector2D, CellRegion> regionMap;

        @Override
        public MembraneRegionStep graph(AutomatonGraph automatonGraph) {
            graph = automatonGraph;
            return this;
        }

        @Override
        public FinalStep membraneRegion(CellRegion innerRegion, CellRegion membraneRegion) {
            this.innerRegion = innerRegion;
            this.membraneRegion = membraneRegion;
            return this;
        }

        @Override
        public MembraneRegionStep regionMapping(Map<Vector2D, CellRegion> regionMap) {
            this.regionMap = regionMap;
            return this;
        }

    }

    public static class LinearMembraneBuilder extends GeneralMembraneBuilder implements VectorStep, InnerPointStep {

        private List<Vector2D> vectors;
        private Vector2D innerPoint;

        @Override
        public InnerPointStep vectors(List<Vector2D> sortedVectors) {
            vectors = sortedVectors;
            return this;
        }

        @Override
        public InnerPointStep vectors(Vector2D... sortedVectors) {
            vectors = Arrays.asList(sortedVectors);
            return this;
        }

        @Override
        public GraphStep innerPoint(Vector2D innerPoint) {
            this.innerPoint = innerPoint;
            return this;
        }


        @Override
        public Membrane build() {
            // create uniform mapping if none was specified
            if (regionMap == null) {
                regionMap = new LinkedHashMap<>();
                for (Vector2D vector : vectors) {
                    regionMap.put(vector, membraneRegion);
                }
            }
            return MembraneFactory.createLinearMembrane(vectors, innerRegion, membraneRegion, innerPoint, graph, regionMap);
        }
    }

    public static class ClosedMembraneBuilder extends GeneralMembraneBuilder implements ClosedVectorStep {

        private List<Vector2D> vectors;

        @Override
        public GraphStep vectors(List<Vector2D> sortedVectors) {
            if (sortedVectors.size() < 3) {
                throw new IllegalArgumentException("Closed Membranes require at least three points to be created.");
            }
            vectors = sortedVectors;
            return this;
        }

        @Override
        public GraphStep circle(Circle circle, int numberOfPoints) {
            vectors = Circles.samplePoints(circle, numberOfPoints);
            return this;
        }

        @Override
        public GraphStep polygon(Polygon polygon) {
            vectors = polygon.getVertices();
            return this;
        }

        @Override
        public Membrane build() {
            // create uniform mapping if none was specified
            if (regionMap == null) {
                regionMap = new LinkedHashMap<>();
                for (Vector2D vector : vectors) {
                    regionMap.put(vector, membraneRegion);
                }
            }
            return MembraneFactory.createClosedMembrane(vectors, innerRegion, membraneRegion, graph, regionMap);
        }

    }

}
