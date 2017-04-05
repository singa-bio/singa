package de.bioforscher.singa.core.parser.xml;

import de.bioforscher.singa.core.parser.AbstractParser;
import de.bioforscher.singa.core.parser.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class AbstractXMLParser extends AbstractParser<String> implements Parser {

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
        return xmlReader;
    }

    public void setXmlReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

}
