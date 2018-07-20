package bio.singa.core.parser;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * The foundation to parse everything xml related.
 *
 * @param <ResultType> The result of the parsing.
 * @author cl
 */
public abstract class AbstractXMLParser<ResultType> extends AbstractHTMLParser<ResultType> {

    /**
     * The XMLReader that is used to parse the fetch result.
     */
    private XMLReader xmlReader;

    /**
     * Creates a new XMLParser and initializes the XMLReader.
     */
    protected AbstractXMLParser() {
        try {
            xmlReader = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            throw new ExceptionInInitializerError(e);
        }
        xmlReader.setErrorHandler(new XMLErrorHandler());
    }

    /**
     * Returns the XMLReader.
     *
     * @return The XMLReader.
     */
    protected XMLReader getXmlReader() {
        return xmlReader;
    }

}
