package bio.singa.chemistry.features.databases.pubchem;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.core.parser.AbstractXMLParser;
import bio.singa.features.identifiers.PubChemIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UncheckedIOException;

public class PubChemParserService extends AbstractXMLParser<SmallMolecule> {

    private static final Logger logger = LoggerFactory.getLogger(PubChemParserService.class);
    private static final String PUBCHEM_FETCH_URL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/%s/XML/";

    public PubChemParserService(PubChemIdentifier identifier) {
        getXmlReader().setContentHandler(new PubChemContentHandler());
        setResource(String.format(PUBCHEM_FETCH_URL, identifier.getConsecutiveNumber()));
    }

    public static SmallMolecule parse(String pubChemIdentifier) {
        return PubChemParserService.parse(new PubChemIdentifier(pubChemIdentifier));
    }

    public static SmallMolecule parse(PubChemIdentifier pubChemIdentifier) {
        logger.info("Parsing chemical entity with identifier " + pubChemIdentifier + " from " + PubChemDatabase.origin.getName());
        PubChemParserService parser = new PubChemParserService(pubChemIdentifier);
        return parser.parse();
    }

    @Override
    public SmallMolecule parse() {
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
