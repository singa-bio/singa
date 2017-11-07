package de.bioforscher.singa.simulation.parser.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import tec.units.ri.quantity.Quantities;

import java.util.HashMap;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * Currently supports parsing nodes and connecting them with the given edges.
 *
 * @author cl
 */
public class GraphMLContentHandler implements ContentHandler {

    private AutomatonGraph graph;

    private AutomatonNode node;
    private double currentX;
    private double currentY;
    private HashMap<String, ChemicalEntity> speciesMap;

    private String tag;

    public GraphMLContentHandler() {
        graph = new AutomatonGraph();
        speciesMap = new HashMap<>();
        tag = "";
    }

    public AutomatonGraph getGraph() {
        return graph;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // position
        switch (tag) {
            case "x":
                currentX = Double.parseDouble(new String(ch, start, length));
                break;
            case "y":
                currentY = Double.parseDouble(new String(ch, start, length));
                break;
        }
        // chemical entities
        if (tag.startsWith("CHEBI")) {
            Double value = Double.parseDouble(new String(ch, start, length));
            node.setConcentration(speciesMap.get(tag), Quantities.getQuantity(value, MOLE_PER_LITRE));
        }
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "data":
                tag = "";
                break;
            case "node":
                node.setPosition(new Vector2D(currentX, currentY));
                graph.addNode(node);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        switch (qName) {
            case "key":
                String chEBIIdentifier = atts.getValue("id");
                // parse species that are present as keys
                if (chEBIIdentifier.startsWith("CHEBI")) {
                    Species entity = ChEBIParserService.parse(chEBIIdentifier);
                    speciesMap.put(chEBIIdentifier, entity);
                }
                break;
            case "data":
                tag = atts.getValue("key");
                break;
            case "node":
                int nodeId = Integer.parseInt(atts.getValue("id"));
                node = new AutomatonNode(nodeId);
                break;
            case "edge":
                int edgeId = Integer.parseInt(atts.getValue("id"));
                AutomatonNode source = graph.getNode(Integer.parseInt(atts.getValue("source")));
                AutomatonNode target = graph.getNode(Integer.parseInt(atts.getValue("target")));
                graph.addEdgeBetween(edgeId, source, target);
                break;
        }

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

}
