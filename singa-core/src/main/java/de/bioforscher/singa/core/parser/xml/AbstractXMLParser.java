package de.bioforscher.singa.core.parser.xml;

import de.bioforscher.singa.core.parser.rest.AbstractHTMLParser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class AbstractXMLParser<ResultType> extends AbstractHTMLParser<ResultType> {

    private XMLReader xmlReader;

    public AbstractXMLParser() {
        try {
            this.xmlReader = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        this.xmlReader.setErrorHandler(new XMLErrorHandler());
    }

    public XMLReader getXmlReader() {
        return this.xmlReader;
    }

    public void setXmlReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

}
