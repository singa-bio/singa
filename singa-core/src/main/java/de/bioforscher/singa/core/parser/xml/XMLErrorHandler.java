package de.bioforscher.singa.core.parser.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Your average all purpose SAX Error-Handler.
 *
 * @author Christoph Leberecht
 */
public class XMLErrorHandler implements ErrorHandler {

    @Override
    public void error(SAXParseException ex) throws SAXException {

        System.out.println("An error has occured.\n" +
                ex.getSystemId() + " in line " + ex.getLineNumber() + " :\n" +
                ex.getMessage());

    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {

        System.out.println("A fatal error has occured.\n" +
                ex.getSystemId() + " in line " + ex.getLineNumber() + " :\n" +
                ex.getMessage());
        throw ex;

    }

    @Override
    public void warning(SAXParseException ex) throws SAXException {

        System.out.println("Warning:\n" +
                ex.getSystemId() + " in line " + ex.getLineNumber() + " :\n" +
                ex.getMessage());
        throw ex;

    }

}