package de.bioforscher.singa.structure.parser.plip;

import de.bioforscher.singa.core.parser.AbstractXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;

/**
 * @author cl
 */
public class PlipParser extends AbstractXMLParser<InteractionContainer> {

    private static final Logger logger = LoggerFactory.getLogger(PlipParser.class);

    private PlipParser(String pdbIdentifier, InputStream inputStream) {
        logger.info("Parsing interactions for {}", pdbIdentifier);
        getXmlReader().setContentHandler(new PlipContentHandler(pdbIdentifier));
        setFetchResult(inputStream);
    }

    private PlipParser(String pdbIdentifier) {
        logger.info("Parsing interactions for {}", pdbIdentifier);
        getXmlReader().setContentHandler(new PlipContentHandler(pdbIdentifier));
    }

    public static InteractionContainer parse(String pdbIdentifier, String plipXml) {
        PlipParser parser = new PlipParser(pdbIdentifier);
        try {
            parser.getXmlReader().parse(new InputSource(new StringReader(plipXml)));
            return ((PlipContentHandler) parser.getXmlReader().getContentHandler()).getInteractionContainer();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse XML from fetch result, the server seems to be unavailable.", e);
        } catch (SAXException e) {
            throw new IllegalStateException("Could not parse XML from fetch result, the XML seems to be malformed.", e);
        }
    }

    public static InteractionContainer parse(String pdbIdentifier, InputStream inputStream) {
        PlipParser parser = new PlipParser(pdbIdentifier, inputStream);
        try {
            parser.getXmlReader().parse(new InputSource(parser.getFetchResult()));
            return ((PlipContentHandler) parser.getXmlReader().getContentHandler()).getInteractionContainer();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse XML from fetch result, the server seems to be unavailable.", e);
        } catch (SAXException e) {
            throw new IllegalStateException("Could not parse XML from fetch result, the XML seems to be malformed.", e);
        }
    }

    @Override
    public InteractionContainer parse() {
        parseXML();
        return ((PlipContentHandler) getXmlReader().getContentHandler()).getInteractionContainer();
    }

    private void parseXML() {
        fetchResource();
        // parse xml
        try {
            getXmlReader().parse(new InputSource(getFetchResult()));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse xml from fetch result, the server seems to be unavailable.", e);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
