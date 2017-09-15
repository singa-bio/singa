package de.bioforscher.singa.simulation.parser.graphs;

import de.bioforscher.singa.core.parser.AbstractXMLParser;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

public class GraphMLParserService extends AbstractXMLParser<AutomatonGraph> {

    private static final Logger logger = LoggerFactory.getLogger(GraphMLParserService.class);

    public GraphMLParserService(String filePath) {
        getXmlReader().setContentHandler(new GraphMLContentHandler());
        setResource(filePath);
    }

    @Override
    public AutomatonGraph parse() {
        logger.debug("Parsing graph from file {}.", getFetchResult());
        try {
            this.getXmlReader().parse(getResource());
        } catch (IOException | SAXException e) {
            logger.error("No graph could be parsed from file {}.", getFetchResult());
            e.printStackTrace();
        }
        return ((GraphMLContentHandler) this.getXmlReader().getContentHandler()).getGraph();
    }
}
