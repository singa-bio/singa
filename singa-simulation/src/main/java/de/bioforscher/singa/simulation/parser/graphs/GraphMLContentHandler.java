package de.bioforscher.singa.simulation.parser.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.chebi.ChEBIParserService;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import tec.units.ri.quantity.Quantities;

import java.util.HashMap;

import static de.bioforscher.singa.units.UnitProvider.MOLE_PER_LITRE;

/**
 * Currently supports parsing nodes and connecting them with the given edges.
 *
 * @author Christoph Leberecht
 */
public class GraphMLContentHandler implements ContentHandler {

    private AutomatonGraph graph;

    private BioNode node;
    private double currentX;
    private double currentY;
    private HashMap<String, ChemicalEntity> speciesMap;

    private String tag;

    public GraphMLContentHandler() {
        this.graph = new AutomatonGraph();
        this.speciesMap = new HashMap<>();
        this.tag = "";
    }

    public AutomatonGraph getGraph() {
        return this.graph;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // position
        switch (this.tag) {
            case "x":
                this.currentX = Double.parseDouble(new String(ch, start, length));
                break;
            case "y":
                this.currentY = Double.parseDouble(new String(ch, start, length));
                break;
        }
        // chemical entities
        if (this.tag.startsWith("CHEBI")) {
            Double value = Double.parseDouble(new String(ch, start, length));
            this.node.setConcentration(this.speciesMap.get(this.tag), Quantities.getQuantity(value, MOLE_PER_LITRE));
        }
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "data":
                this.tag = "";
                break;
            case "node":
                this.node.setPosition(new Vector2D(this.currentX, this.currentY));
                this.graph.addNode(this.node);
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
                String chebiId = atts.getValue("id");
                // parse species that are present as keys
                if (chebiId.startsWith("CHEBI")) {
                    ChEBIParserService service = new ChEBIParserService(chebiId);
                    Species entity = service.fetchSpecies();
                    this.speciesMap.put(chebiId, entity);
                }
                break;
            case "data":
                this.tag = atts.getValue("key");
                break;
            case "node":
                int nodeId = Integer.parseInt(atts.getValue("id"));
                this.node = new BioNode(nodeId);
                break;
            case "edge":
                int edgeId = Integer.parseInt(atts.getValue("id"));
                BioNode source = this.graph.getNode(Integer.parseInt(atts.getValue("source")));
                BioNode target = this.graph.getNode(Integer.parseInt(atts.getValue("target")));
                this.graph.addEdgeBetween(edgeId, source, target);
                for (ChemicalEntity entity : this.speciesMap.values()) {
                    this.graph.getEdge(edgeId).addPermeability(entity, 1.0);
                }
                break;
        }

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

}
