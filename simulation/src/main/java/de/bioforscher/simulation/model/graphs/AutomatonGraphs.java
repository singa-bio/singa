package de.bioforscher.simulation.model.graphs;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.graphs.model.RegularNode;
import de.bioforscher.mathematics.graphs.model.UndirectedEdge;
import de.bioforscher.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.units.quantities.MolarConcentration;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            for (ChemicalEntity entity : node.getConcentrations().keySet()) {
                results.put(entity.getName(), entity);
            }
        }
        return results;
    }

    /**
     * Populates the given graph with the given chemical entity in the desired concentration.
     *
     * @param graph The graph to populate
     * @param entity The chemical entity
     * @param concentration The desired concentration.
     */
    public static void fillGraphWithEntity(AutomatonGraph graph, ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        for (BioNode node : graph.getNodes()) {
            node.addEntity(entity, concentration);
        }
    }

    /**
     * Copys the structure (nodes and edges) of an {@link UndirectedGraph} to a {@link AutomatonGraph}. No new data is
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
