package bio.singa.mathematics.graphs.parser;

import bio.singa.core.parser.AbstractXMLParser;
import bio.singa.mathematics.graphs.model.UndirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

public class GraphMLParserService extends AbstractXMLParser<UndirectedGraph> {

    private static final Logger logger = LoggerFactory.getLogger(GraphMLParserService.class);

    public GraphMLParserService(String filePath) {
        getXmlReader().setContentHandler(new GraphMLContentHandler());
        setResource(filePath);
    }

    @Override
    public UndirectedGraph parse() {
        logger.debug("Parsing graph from file {}.", getFetchResult());
        try {
            getXmlReader().parse(getResource());
        } catch (IOException | SAXException e) {
            logger.error("No graph could be parsed from file {}.", getFetchResult());
            e.printStackTrace();
        }
        return ((GraphMLContentHandler) getXmlReader().getContentHandler()).getGraph();
    }
}
