package de.bioforscher.simulation.parser.graphs;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.chebi.ChEBIParserService;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.simulation.model.graphs.BioNode;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import tec.units.ri.quantity.Quantities;

import java.util.HashMap;

import static de.bioforscher.units.UnitProvider.MOLE_PER_LITRE;

/**
 * Currently supports parsing nodes and connecting them with the given edges.
 *
 * @author Christoph Leberecht
 */
public class GraphMLContentHandler implements ContentHandler {

    private AutomatonGraph graph;

    private BioNode node;
    private double[] currentPosition;
    private HashMap<String, ChemicalEntity> speciesMap;

    private String tag;

    public GraphMLContentHandler() {
        this.graph = new AutomatonGraph();
        this.speciesMap = new HashMap<>();
        this.currentPosition = new double[2];
        this.tag = "";
    }

    public AutomatonGraph getGraph() {
        return this.graph;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        switch (this.tag) {
            case "x":
                double x = Double.parseDouble(new String(ch, start, length));
                this.currentPosition[Vector2D.X_INDEX] = x;
                break;
            case "y":
                double y = Double.parseDouble(new String(ch, start, length));
                this.currentPosition[Vector2D.X_INDEX] = y;
                this.node.setPosition(new Vector2D(this.currentPosition));
                this.currentPosition = new double[2];
                break;
        }

        if (this.tag.startsWith("CHEBI")) {
            Double value = Double.parseDouble(new String(ch, start, length));
            this.node.setConcentration(this.speciesMap.get(this.tag), Quantities.getQuantity(value, MOLE_PER_LITRE));
        } else if (this.tag.equals("P42212")) {
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
                if (chebiId.startsWith("CHEBI")) {
                    ChEBIParserService service = new ChEBIParserService(chebiId);
                    Species entity = service.fetchSpecies();
                    this.speciesMap.put(chebiId, entity);
                } else if (chebiId.equals("P42212")) {
                    Enzyme gfp = new Enzyme.Builder("P42212").name("GFP").molarMass(26886.0).build();
                    this.speciesMap.put("P42212", gfp);
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
