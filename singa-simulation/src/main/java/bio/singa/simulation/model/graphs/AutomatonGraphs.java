package bio.singa.simulation.model.graphs;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.graphs.grid.GridEdge;
import bio.singa.mathematics.graphs.grid.GridGraph;
import bio.singa.mathematics.graphs.grid.GridNode;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.sections.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author cl
 */
public class AutomatonGraphs {

    private static final Logger logger = LoggerFactory.getLogger(AutomatonGraphs.class);

    private AutomatonGraphs() {

    }

    /**
     * Creates and returns a map that contains all chemical entities that are present in the given graph as values and
     * with the name of the entity as key.
     *
     * @param graph A graph with species
     * @return All chemical entities in the graph.
     */
    public static Map<String, ChemicalEntity> generateMapOfEntities(AutomatonGraph graph) {
        Map<String, ChemicalEntity> results = new HashMap<>();
        for (AutomatonNode node : graph.getNodes()) {
            ConcentrationContainer concentrationContainer = node.getConcentrationContainer();
            for (ConcentrationPool pool : concentrationContainer.getPoolsOfConcentration()) {
                for (ChemicalEntity entity : pool.getReferencedEntities()) {
                    results.put(entity.getIdentifier().toString(), entity);
                }
            }
        }
        return results;
    }

    public static AutomatonGraph createRectangularAutomatonGraph(int numberOfColumns, int numberOfRows) {
        return AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(numberOfColumns, numberOfRows,
                new Rectangle(Environment.getSimulationExtend(), Environment.getSimulationExtend())));
    }

    /**
     * Copies the structure (nodes and edges) of an {@link UndirectedGraph} to a {@link AutomatonGraph}. No new data is
     * generated. Indices are persistent. Both Graphs are independently modifiable.
     *
     * @param gridGraph The graph to be cast
     * @return The generated automaton graph.
     */
    public static AutomatonGraph useStructureFrom(GridGraph gridGraph) {
        AutomatonGraph bioGraph = new AutomatonGraph(gridGraph.getNumberOfColumns(), gridGraph.getNumberOfRows());
        // copy nodes
        for (GridNode regularNode : gridGraph.getNodes()) {
            RectangularCoordinate coordinate = regularNode.getIdentifier();
            AutomatonNode node = new AutomatonNode(coordinate);
            node.setPosition(regularNode.getPosition());
            bioGraph.addNode(node);
        }
        // copy edges
        for (GridEdge undirectedEdge : gridGraph.getEdges()) {
            int identifier = undirectedEdge.getIdentifier();
            bioGraph.addEdgeBetween(identifier, bioGraph.getNode(undirectedEdge.getSource().getIdentifier()), bioGraph.getNode(undirectedEdge.getTarget().getIdentifier()));
        }
        return bioGraph;
    }

    public static AutomatonGraph singularGraph() {
        AutomatonGraph automatonGraph = new AutomatonGraph(1, 1);
        AutomatonNode node = new AutomatonNode(new RectangularCoordinate(0, 0));
        node.setPosition(new Vector2D(0.0, 0.0));
        automatonGraph.addNode(node);
        return automatonGraph;
    }

    public static AutomatonGraph singularGraph(CellRegion region) {
        AutomatonGraph automatonGraph = new AutomatonGraph(1, 1);
        AutomatonNode node = new AutomatonNode(new RectangularCoordinate(0, 0));
        node.setPosition(new Vector2D(0.0, 0.0));
        node.setCellRegion(region);
        automatonGraph.addNode(node);
        return automatonGraph;
    }

    public static CellRegion splitRectangularGraphWithMembrane(AutomatonGraph graph, CellSubsection innerSection, CellSubsection outerSection, boolean switchSides) {
        logger.debug("Splitting graph in inner ({}) and outer ({}) compartment with membrane.", innerSection.getIdentifier(), outerSection.getIdentifier());
        // distribute nodes to sections
        CellRegion outer = new CellRegion("Outer");
        outer.addSubsection(CellTopology.INNER, outerSection);
        CellRegion inner = new CellRegion("Inner");
        inner.addSubsection(CellTopology.INNER, innerSection);
        CellRegion membrane = new CellRegion("Membrane");
        membrane.addSubsection(CellTopology.INNER, innerSection);
        membrane.addSubsection(CellTopology.MEMBRANE, new CellSubsection("Membrane"));
        membrane.addSubsection(CellTopology.OUTER, outerSection);

        int numberOfColumns = graph.getNumberOfColumns();
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() < (numberOfColumns / 2)) {
                // left half is outer
                if (switchSides) {
                    node.setCellRegion(inner);
                } else {
                    node.setCellRegion(outer);
                }
            } else if (node.getIdentifier().getColumn() == (numberOfColumns / 2)) {
                // middle is membrane
                node.setCellRegion(membrane);
            } else {
                // right half is inner
                if (switchSides) {
                    node.setCellRegion(outer);
                } else {
                    node.setCellRegion(inner);
                }
            }
        }
        // reference sections in graph
        graph.addCellRegion(outer);
        graph.addCellRegion(inner);
        graph.addCellRegion(membrane);
        return membrane;
    }

    public static Set<AutomatonNode> circleRegion(AutomatonGraph graph, CellRegion membraneRegion, RectangularCoordinate centre, int radius) {
        Set<AutomatonNode> nodes = new HashSet<>();
        int x0 = centre.getColumn();
        int y0 = centre.getRow();

        int x = radius - 1;
        int y = 0;
        int dx = 1;
        int dy = 1;
        int err = dx - (radius << 1);
        // collect nodes
        while (x >= y) {

            nodes.add(graph.getNode(x + x0, y + y0));
            nodes.add(graph.getNode(y + x0, x + y0));
            nodes.add(graph.getNode(-x + x0, y + y0));
            nodes.add(graph.getNode(-y + x0, x + y0));
            nodes.add(graph.getNode(-x + x0, -y + y0));
            nodes.add(graph.getNode(-y + x0, -x + y0));
            nodes.add(graph.getNode(x + x0, -y + y0));
            nodes.add(graph.getNode(y + x0, -x + y0));

            if (err <= 0) {
                y++;
                err += dy;
                dy += 2;
            }

            if (err > 0) {
                x--;
                dx += 2;
                err += dx - (radius << 1);
            }
        }
        // set region
        for (AutomatonNode node : nodes) {
            node.setCellRegion(membraneRegion);
        }
        // return them
        return nodes;
    }

    public static void fillRegion(AutomatonGraph graph, CellRegion innerRegion, RectangularCoordinate centre, int radius) {
        int x0 = centre.getColumn();
        int y0 = centre.getRow();

        int x = radius - 1;
        int y = 0;
        int xChange = 1 - (radius << 1);
        int yChange = 0;
        int radiusError = 0;

        while (x >= y) {
            for (int i = x0 - x; i <= x0 + x; i++) {
                graph.getNode(i, y0 + y).setCellRegion(innerRegion);
                graph.getNode(i, y0 - y).setCellRegion(innerRegion);
            }
            for (int i = x0 - y; i <= x0 + y; i++) {
                graph.getNode(i, y0 + x).setCellRegion(innerRegion);
                graph.getNode(i, y0 - x).setCellRegion(innerRegion);
            }

            y++;
            radiusError += yChange;
            yChange += 2;
            if (((radiusError << 1) + xChange) > 0) {
                x--;
                radiusError += xChange;
                xChange += 2;
            }
        }
    }

}
