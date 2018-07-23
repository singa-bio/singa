package bio.singa.core.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Your average all purpose SAX Error-Handler.
 *
 * @author cl
 */
public class XMLErrorHandler implements ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(XMLErrorHandler.class);

    @Override
    public void error(SAXParseException exception) throws SAXException {
        logger.error("An error has occurred {} in line {} : {}", exception.getSystemId(), exception.getLineNumber(), exception.getMessage());
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        logger.error("An fatal error has occurred {} in line {} : {}", exception.getSystemId(), exception.getLineNumber(), exception.getMessage());
        throw exception;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        logger.warn("Warning {} in line {} : {}", exception.getSystemId(), exception.getLineNumber(), exception.getMessage());
        throw exception;
    }

}