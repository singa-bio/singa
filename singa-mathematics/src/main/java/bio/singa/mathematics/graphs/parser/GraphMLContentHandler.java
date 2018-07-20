package bio.singa.mathematics.graphs.parser;

import bio.singa.mathematics.graphs.model.RegularNode;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import bio.singa.mathematics.vectors.Vector2D;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/**
 * Currently supports parsing nodes and connecting them with the given edges.
 *
 * @author cl
 */
public class GraphMLContentHandler implements ContentHandler {

    private final UndirectedGraph graph;
    private RegularNode node;
    private double currentX;
    private double currentY;
    private String tag;

    public GraphMLContentHandler() {
        graph = new UndirectedGraph();
        tag = "";
    }

    public UndirectedGraph getGraph() {

        return graph;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        // position
        switch (tag) {
            case "x":
                currentX = Double.parseDouble(new String(ch, start, length));
                break;
            case "y":
                currentY = Double.parseDouble(new String(ch, start, length));
                break;
        }
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
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
    public void endPrefixMapping(String prefix) {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
    }

    @Override
    public void processingInstruction(String target, String data) {
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(String name) {
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {

        switch (qName) {
            case "data":
                tag = atts.getValue("key");
                break;
            case "node":
                int nodeId = Integer.parseInt(atts.getValue("id"));
                node = new RegularNode(nodeId);
                break;
            case "edge":
                int edgeId = Integer.parseInt(atts.getValue("id"));
                RegularNode source = graph.getNode(Integer.parseInt(atts.getValue("source")));
                RegularNode target = graph.getNode(Integer.parseInt(atts.getValue("target")));
                graph.addEdgeBetween(edgeId, source, target);
                break;
        }

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {

    }

}
