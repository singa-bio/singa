package de.bioforscher.singa.simulation.parser.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class GraphMLParserServiceTest {

    @Test
    public void shouldParseGraphXML() {
        String file = Thread.currentThread().getContextClassLoader().getResource("graphml_sample_01.xml").getFile();
        GraphMLParserService service = new GraphMLParserService(file);
        AutomatonGraph graph = service.parse();
        // determine correct size
        assertEquals(graph.getNodes().size(), 100);
        assertEquals(graph.getEdges().size(), 180);
        // identifiers are correctly assigned
        BioNode node37 = graph.getNode(37);
        // position is correctly assigned
        assertEquals(node37.getPosition().getX(), 436.3636363636364, 0.0);
        assertEquals(node37.getPosition().getY(), 218.1818181818182, 0.0);
        // concentrations are empty
        for (ChemicalEntity entity: node37.getAllReferencedEntities()) {
            assertEquals(node37.getConcentration(entity).getValue().doubleValue(), 0.0, 0.0);
        }
        // concentrations are set
        BioNode node72 = graph.getNode(72);
        for (ChemicalEntity entity: node72.getAllReferencedEntities()) {
            assertEquals(node72.getConcentration(entity).getValue().doubleValue(), 1.0, 0.0);
        }
        // connections are set
        assertTrue(node72.hasNeighbour(graph.getNode(71)));
        assertTrue(node72.hasNeighbour(graph.getNode(73)));
        assertTrue(node72.hasNeighbour(graph.getNode(62)));
        assertTrue(node72.hasNeighbour(graph.getNode(82)));
    }


}