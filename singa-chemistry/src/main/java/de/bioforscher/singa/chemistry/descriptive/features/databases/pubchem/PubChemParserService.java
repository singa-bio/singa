package de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot.UniProtParserService;
import de.bioforscher.singa.core.identifier.PubChemIdentifier;
import de.bioforscher.singa.core.parser.AbstractXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UncheckedIOException;

public class PubChemParserService extends AbstractXMLParser<Species> {

    private static final Logger logger = LoggerFactory.getLogger(UniProtParserService.class);
    private static final String PUBCHEM_FETCH_URL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/%s/XML/";

    public PubChemParserService(PubChemIdentifier identifier) {
        getXmlReader().setContentHandler(new PubChemContentHandler());
        setResource(String.format(PUBCHEM_FETCH_URL, identifier.getConsecutiveNumber()));
    }

    public static Species parse(String pubChemIdentifier) {
        return PubChemParserService.parse(new PubChemIdentifier(pubChemIdentifier));
    }

    public static Species parse(PubChemIdentifier pubChemIdentifier) {
        logger.info("Parsing chemical entity with identifier " + pubChemIdentifier + " from " + PubChemDatabase.origin.getName());
        PubChemParserService parser = new PubChemParserService(pubChemIdentifier);
        return parser.parse();
    }

    @Override
    public Species parse() {
        parseXML();
        return ((PubChemContentHandler) getXmlReader().getContentHandler()).getSpecies();
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
