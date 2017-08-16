package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.*;
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
    private static final Rectangle defaultBoundingBox = new Rectangle(600, 600);

    private AutomatonGraphs() {

    }

    /**
     * Creates and returns a map that contains all chemical entities that are present in the given graph as values and
     * with the name of the entity as key.
     *
     * @param graph A graph with species
     * @return All chemical entities in the graph.
     */
    public static Map<String, ChemicalEntity<?>> generateMapOfEntities(AutomatonGraph graph) {
        Map<String, ChemicalEntity<?>> results = new HashMap<>();
        for (BioNode node : graph.getNodes()) {
            for (ChemicalEntity<?> entity : node.getAllReferencedEntities()) {
                results.put(entity.getName(), entity);
            }
        }
        return results;
    }

    public static AutomatonGraph createRectangularAutomatonGraph(int numberOfColumns, int numberOfRows) {
        return AutomatonGraphs.copyStructureToBioGraph(Graphs.buildGridGraph(numberOfColumns, numberOfRows, defaultBoundingBox, false));
    }

    /**
     * Copies the structure (nodes and edges) of an {@link UndirectedGraph} to a {@link AutomatonGraph}. No new data is
     * generated. Indices are persistent. Both Graphs are independently modifiable.
     *
     * @param undirectedGraph The graph to be cast
     * @return The generated automaton graph.
     */
    public static AutomatonGraph copyStructureToBioGraph(UndirectedGraph undirectedGraph) {
        AutomatonGraph bioGraph = new AutomatonGraph();
        // copy nodes
        for (RegularNode regularNode : undirectedGraph.getNodes()) {
            int id = regularNode.getIdentifier();
            BioNode bioNode = new BioNode(id);
            bioNode.setPosition(regularNode.getPosition());
            bioGraph.addNode(bioNode);
        }
        // copy edges
        for (UndirectedEdge undirectedEdge : undirectedGraph.getEdges()) {
            int id = undirectedEdge.getIdentifier();
            bioGraph.addEdgeBetween(id, bioGraph.getNode(undirectedEdge.getSource().getIdentifier()),
                    bioGraph.getNode(undirectedEdge.getTarget().getIdentifier()));
        }
        return bioGraph;
    }

    public static Membrane splitRectangularGraphWithMembrane(AutomatonGraph graph, GridCoordinateConverter converter,
                                                                   EnclosedCompartment innerSection, CellSection outerSection) {
        logger.debug("Splitting graph in inner ({}) and outer ({}) compartment with membrane.", innerSection.getName(),
                outerSection.getName());
        // column wise (vertical split)
        int numberOfColumns = converter.getNumberOfColumns();
        // create Membrane for enclosed compartment
        Membrane membrane = Membrane.forCompartment(innerSection);
        // distribute nodes to sections
        for (BioNode node : graph.getNodes()) {
            if (converter.convert(node.getIdentifier()).getY() < (numberOfColumns / 2)) {
                // left half is outer
                node.setCellSection(outerSection);
            } else if (converter.convert(node.getIdentifier()).getY() == (numberOfColumns / 2)) {
                // middle is membrane
                node.setCellSection(membrane);
            } else {
                // right half is inner
                node.setCellSection(innerSection);
            }
        }
        // reference sections in graph
        graph.addSection(outerSection);
        graph.addSection(innerSection);
        graph.addSection(membrane);
        // membrane has to be initialized after every node knows its section
        membrane.initializeNodes(graph);

        return membrane;
    }



}
