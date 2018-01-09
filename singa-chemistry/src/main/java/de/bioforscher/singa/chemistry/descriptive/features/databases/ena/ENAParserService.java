package de.bioforscher.singa.chemistry.descriptive.features.databases.ena;

import de.bioforscher.singa.core.biology.NucleotideSequence;
import de.bioforscher.singa.core.identifier.ENAAccessionNumber;
import de.bioforscher.singa.core.parser.AbstractXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author cl
 */
public class ENAParserService extends AbstractXMLParser<NucleotideSequence> {

    private static final Logger logger = LoggerFactory.getLogger(ENAParserService.class);
    private static final String ENA_FETCH_URL = "https://www.ebi.ac.uk/ena/data/view/%s&display=xml";

    public ENAParserService(ENAAccessionNumber enaAccessionNumber) {
        getXmlReader().setContentHandler(new ENAContentHandler());
        setResource(String.format(ENA_FETCH_URL, enaAccessionNumber.getIdentifier()));
    }

    public static NucleotideSequence parse(ENAAccessionNumber enaAccessionNumber) {
        logger.info("Parsing sequence with identifier {} from ENA.", enaAccessionNumber.getIdentifier());
        ENAParserService parser = new ENAParserService(enaAccessionNumber);
        return parser.parse();
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

    @Override
    public NucleotideSequence parse() {
        parseXML();
        return ((ENAContentHandler) getXmlReader().getContentHandler()).getSequence();
    }
}
