package de.bioforscher.simulation.parser.graphs;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.simulation.model.graphs.BioEdge;
import de.bioforscher.simulation.model.graphs.BioNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

/**
 * This class contains a single static method to export a
 * Graph to a GraphML
 * file.
 *
 * @author Christoph Leberecht
 */
public class GraphMLExportService {

    private static final Logger logger = LoggerFactory.getLogger(GraphMLExportService.class);

    /**
     * Exports a Graph to a GraphML file.
     *
     * @param graph The graph.
     * @param file  The new target file.
     */
    public static void exportGraph(AutomatonGraph graph, File file) {
        logger.info("Writing graph to file {}" , file.getAbsolutePath());
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("graphml");
            doc.appendChild(rootElement);

            Map<String, ChemicalEntity> entityMap = AutomatonGraphs.generateMapOfEntities(graph);
            // x coordiante key
            Element keyXCoordinate = doc.createElement("key");
            keyXCoordinate.setAttribute("id", "x");
            keyXCoordinate.setAttribute("for", "node");
            keyXCoordinate.setAttribute("attr.name", "xCoordinate");
            keyXCoordinate.setAttribute("attr.type", "double");
            rootElement.appendChild(keyXCoordinate);

            // y coordiante key
            Element keyYCoordinate = doc.createElement("key");
            keyYCoordinate.setAttribute("id", "y");
            keyYCoordinate.setAttribute("for", "node");
            keyYCoordinate.setAttribute("attr.name", "yCoordinate");
            keyYCoordinate.setAttribute("attr.type", "double");
            rootElement.appendChild(keyYCoordinate);

            // species concentrations keys
            for (ChemicalEntity entity : entityMap.values()) {

                Element keyConcentration = doc.createElement("key");
                keyConcentration.setAttribute("id", entity.getIdentifier().toString());
                keyConcentration.setAttribute("for", "node");
                keyConcentration.setAttribute("attr.name", "Concentration_" + entity.getIdentifier());
                keyConcentration.setAttribute("attr.type", "double");
                rootElement.appendChild(keyConcentration);

                // concentration default
                Element concentrationDefault = doc.createElement("default");
                concentrationDefault.appendChild(doc.createTextNode("0.0"));
                keyConcentration.appendChild(concentrationDefault);
            }

            // Graph
            Element graphElement = doc.createElement("graph");
            graphElement.setAttribute("id", "BioGraph");
            graphElement.setAttribute("edgedefault", "undirected");
            rootElement.appendChild(graphElement);

            // Nodes
            for (BioNode node : graph.getNodes()) {
                Element nodeElement = doc.createElement("node");
                nodeElement.setAttribute("id", String.valueOf(node.getIdentifier()));
                graphElement.appendChild(nodeElement);
                Element nodeX = doc.createElement("data");
                nodeX.setAttribute("key", "x");
                nodeX.appendChild(doc.createTextNode(String.valueOf(node.getPosition().getX())));
                nodeElement.appendChild(nodeX);
                Element nodeY = doc.createElement("data");
                nodeY.setAttribute("key", "y");
                nodeY.appendChild(doc.createTextNode(String.valueOf(node.getPosition().getY())));
                nodeElement.appendChild(nodeY);

                for (ChemicalEntity entity : entityMap.values()) {
                    Element nodeData = doc.createElement("data");
                    nodeData.setAttribute("key", entity.getIdentifier().toString());
                    nodeData.appendChild(doc
                            .createTextNode(String.valueOf(node.getConcentration(entity).getValue().doubleValue())));
                    nodeElement.appendChild(nodeData);
                }
            }

            // Edges
            for (BioEdge edge : graph.getEdges()) {
                Element edgeElement = doc.createElement("edge");
                edgeElement.setAttribute("id", String.valueOf(edge.getIdentifier()));
                edgeElement.setAttribute("source", String.valueOf(edge.getSource().getIdentifier()));
                edgeElement.setAttribute("target", String.valueOf(edge.getTarget().getIdentifier()));
                graphElement.appendChild(edgeElement);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

}
