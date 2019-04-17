package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class MembraneBuilder {

    public static VectorStep linear() {
        return new LinearMembraneBuilder();
    }

    public interface VectorStep {

        GraphStep sorted(Collection<Vector2D> sortedVectors);

        GraphStep sorted(Vector2D... sortedVectors);

        GraphStep unsorted(Collection<Vector2D> unsortedVectors);

        GraphStep unsorted(Vector2D... sortedVectors);

    }

    public interface GraphStep {

        GlobalClipperStep graph(AutomatonGraph automatonGraph);

    }

    public interface GlobalClipperStep {

        RegionMappingStep clipper(Rectangle rectangle);

    }

    public interface MembraneRegionStep {

        DirectionStep membraneRegion(CellRegion innerRegion, CellRegion membraneRegion);

    }

    public interface RegionMappingStep extends MembraneRegionStep {

        MembraneRegionStep regionMapping(Map<Vector2D, CellRegion> regionMap);

    }


    public interface DirectionStep {

        FinalStep direction(NeumannRectangularDirection direction);

    }


    public interface FinalStep {

        Membrane build();

    }

    public static class LinearMembraneBuilder implements VectorStep, GraphStep, GlobalClipperStep, RegionMappingStep, DirectionStep, FinalStep {

        private Collection<Vector2D> vectors;
        private boolean isSorted;
        private AutomatonGraph graph;
        private Rectangle clipper;
        private CellRegion innerRegion;
        private CellRegion membraneRegion;
        private Map<Vector2D, CellRegion> regionMap;
        private NeumannRectangularDirection direction;

        public LinearMembraneBuilder() {

        }

        @Override
        public GraphStep sorted(Collection<Vector2D> sortedVectors) {
            vectors = sortedVectors;
            isSorted = true;
            return this;
        }

        @Override
        public GraphStep sorted(Vector2D... sortedVectors) {
            vectors = Arrays.asList(sortedVectors);
            isSorted = true;
            return this;
        }

        @Override
        public GraphStep unsorted(Collection<Vector2D> unsortedVectors) {
            vectors = unsortedVectors;
            isSorted = false;
            return this;
        }

        @Override
        public GraphStep unsorted(Vector2D... unsortedVectors) {
            vectors = Arrays.asList(unsortedVectors);
            isSorted = false;
            return this;
        }

        @Override
        public GlobalClipperStep graph(AutomatonGraph automatonGraph) {
            graph = automatonGraph;
            return this;
        }

        @Override
        public RegionMappingStep clipper(Rectangle rectangle) {
            clipper = rectangle;
            return this;
        }

        @Override
        public DirectionStep membraneRegion(CellRegion innerRegion, CellRegion membraneRegion) {
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
        public FinalStep direction(NeumannRectangularDirection direction) {
            this.direction = direction;
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
            return MembraneFactory.createLinearMembrane(vectors, innerRegion, membraneRegion, direction, graph, regionMap, clipper, isSorted);
        }
    }

}
