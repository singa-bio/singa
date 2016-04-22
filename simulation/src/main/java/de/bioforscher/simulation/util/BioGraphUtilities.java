package de.bioforscher.simulation.util;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.graphs.model.RegularNode;
import de.bioforscher.mathematics.graphs.model.UndirectedEdge;
import de.bioforscher.mathematics.graphs.model.UndirectedGraph;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.HashMap;
import java.util.Map;

public class BioGraphUtilities {

    public static Map<String, ChemicalEntity> generateMapOfEntities(AutomatonGraph graph) {
        Map<String, ChemicalEntity> results = new HashMap<>();
        for (BioNode node : graph.getNodes()) {
            for (ChemicalEntity entity : node.getConcentrations().keySet()) {
                results.put(entity.getName(), entity);
            }
        }
        return results;
    }

    public static void fillGraphWithSpecies(AutomatonGraph graph, ChemicalEntity entity, double concentration) {
        for (BioNode node : graph.getNodes()) {
            node.addEntity(entity, concentration);
        }
        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(entity, 1.0);
        }
    }

    /**
     * Casts a graph with nodes and edges to a BioGraph. No new data is
     * generated. Indices are persistent.
     *
     * @param undirectedGraph A Graph with normal Edges and Nodes
     * @return A Graph with BioEdges and BioNodes
     */
    public static AutomatonGraph castUndirectedGraphToBioGraph(UndirectedGraph undirectedGraph) {

        AutomatonGraph bioGraph = new AutomatonGraph();

        for (RegularNode regularNode : undirectedGraph.getNodes()) {
            int id = regularNode.getIdentifier();
            BioNode bioNode = new BioNode(id);
            bioNode.setPosition(regularNode.getPosition());
            bioGraph.addNode(bioNode);
        }

        for (UndirectedEdge undirectedEdge : undirectedGraph.getEdges()) {
            int id = undirectedEdge.getIdentifier();
            bioGraph.connect(id, bioGraph.getNode(undirectedEdge.getSource().getIdentifier()),
                    bioGraph.getNode(undirectedEdge.getTarget().getIdentifier()));
        }
        return bioGraph;
    }

    public static void setNodeSpacingToDiameter(Quantity<Length> diameter, int spanningNodes) {
        EnvironmentalVariables.getInstance().setNodeDistance(
                Quantities.getQuantity(diameter.getValue().doubleValue() / (spanningNodes - 1), diameter.getUnit()));
    }

}
