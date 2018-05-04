package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.mathematics.graphs.grid.GridEdge;
import de.bioforscher.singa.mathematics.graphs.grid.GridGraph;
import de.bioforscher.singa.mathematics.graphs.grid.GridNode;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
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
            for (ChemicalEntity entity : node.getAllReferencedEntities()) {
                // TODO check entity equals
                results.put(entity.getIdentifier().toString(), entity);
            }
        }
        return results;
    }

    public static AutomatonGraph createRectangularAutomatonGraph(int numberOfColumns, int numberOfRows) {
        return AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(numberOfColumns, numberOfRows));
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

    public static Membrane splitRectangularGraphWithMembrane(AutomatonGraph graph, EnclosedCompartment innerSection, CellSection outerSection, boolean switchSides) {
        logger.debug("Splitting graph in inner ({}) and outer ({}) compartment with membrane.", innerSection.getName(), outerSection.getName());
        // create Membrane for enclosed compartment
        Membrane membrane = Membrane.forCompartment(innerSection);
        // distribute nodes to sections
        int numberOfColumns = graph.getNumberOfColumns();
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() < (numberOfColumns / 2)) {
                // left half is outer
                if (switchSides) {
                    node.setCellSection(innerSection);
                } else {
                    node.setCellSection(outerSection);
                }
            } else if (node.getIdentifier().getColumn() == (numberOfColumns / 2)) {
                // middle is membrane
                node.setCellSection(membrane);
            } else {
                // right half is inner
                if (switchSides) {
                    node.setCellSection(outerSection);
                } else {
                    node.setCellSection(innerSection);
                }
            }
        }
        // reference sections in graph
        graph.addCellSection(outerSection);
        graph.addCellSection(innerSection);
        graph.addCellSection(membrane);
        // membrane has to be initialized after every node knows its section
        membrane.initializeNodes(graph);

        return membrane;
    }


}
