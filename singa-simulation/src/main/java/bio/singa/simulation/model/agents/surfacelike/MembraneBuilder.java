package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static class LinearMembraneBuilder implements VectorStep, InnerPointStep, GraphStep, RegionMappingStep, FinalStep {

        private List<Vector2D> vectors;
        private Vector2D innerPoint;
        private AutomatonGraph graph;
        private CellRegion innerRegion;
        private CellRegion membraneRegion;
        private Map<Vector2D, CellRegion> regionMap;

        public LinearMembraneBuilder() {

        }

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

        @Override
        public Membrane build() {
            // create uniform mapping if none was specified
            if (regionMap == null) {
                regionMap = new HashMap<>();
                for (Vector2D vector : vectors) {
                    regionMap.put(vector, membraneRegion);
                }
            }
            return MembraneFactory.createLinearMembrane(vectors, innerRegion, membraneRegion, innerPoint, graph, regionMap);
        }
    }

}
