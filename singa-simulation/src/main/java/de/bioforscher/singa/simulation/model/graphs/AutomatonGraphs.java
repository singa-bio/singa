package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.grid.GridEdge;
import de.bioforscher.singa.mathematics.graphs.grid.GridGraph;
import de.bioforscher.singa.mathematics.graphs.grid.GridNode;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.simulation.model.newsections.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
            AutomatonNode bioNode = new AutomatonNode(coordinate);
            bioNode.setPosition(regularNode.getPosition());
            bioGraph.addNode(bioNode);
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
        automatonGraph.addNode(node);
        return automatonGraph;
    }

    public static CellRegion splitRectangularGraphWithMembrane(AutomatonGraph graph, CellSubsection innerSection, CellSubsection outerSection, boolean switchSides) {
        logger.debug("Splitting graph in inner ({}) and outer ({}) compartment with membrane.", innerSection.getIdentifier(), outerSection.getIdentifier());
        // distribute nodes to sections
        CellRegion outer = new CellRegion("Outer");
        outer.addSubSection(CellTopology.INNER, outerSection);
        CellRegion inner = new CellRegion("Inner");
        inner.addSubSection(CellTopology.INNER, innerSection);
        CellRegion membrane = new CellRegion("Membrane");
        membrane.addSubSection(CellTopology.INNER, innerSection);
        membrane.addSubSection(CellTopology.MEMBRANE, new CellSubsection("Membrane"));
        membrane.addSubSection(CellTopology.OUTER, outerSection);

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


}
