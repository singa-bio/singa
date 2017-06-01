package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.mathematics.graphs.model.RegularNode;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedEdge;
import de.bioforscher.singa.mathematics.graphs.model.UndirectedGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class AutomatonGraphs {

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
        for (BioNode node : graph.getNodes()) {
            for (ChemicalEntity entity : node.getAllReferencedEntities()) {
                results.put(entity.getName(), entity);
            }
        }
        return results;
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



}
