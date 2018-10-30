package bio.singa.mathematics.graphs.parser;

import bio.singa.mathematics.graphs.model.RegularNode;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cl
 */
class GraphMLParserServiceTest {

    @Test
    void shouldParseGraphXML() {
        String file = Thread.currentThread().getContextClassLoader().getResource("graphml_sample_01.xml").getFile();
        GraphMLParserService service = new GraphMLParserService(file);
        UndirectedGraph graph = service.parse();
        // determine correct size
        assertEquals(graph.getNodes().size(), 100);
        assertEquals(graph.getEdges().size(), 180);
        RegularNode node37 = graph.getNode(37);
        // identifiers and position are correctly assigned
        assertEquals(node37.getPosition().getX(), 436.3636363636364);
        assertEquals(node37.getPosition().getY(), 218.1818181818182);
        // connections are set
        RegularNode node72 = graph.getNode(72);
        assertTrue(node72.hasNeighbour(graph.getNode(71)));
        assertTrue(node72.hasNeighbour(graph.getNode(73)));
        assertTrue(node72.hasNeighbour(graph.getNode(62)));
        assertTrue(node72.hasNeighbour(graph.getNode(82)));
    }

}