package bio.singa.chemistry.features.databases.chebi;

import bio.singa.chemistry.model.SmallMolecule;
import bio.singa.core.parser.AbstractXMLParser;
import bio.singa.features.identifiers.ChEBIIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ChEBIParserService extends AbstractXMLParser<SmallMolecule> {

    private static final Logger logger = LoggerFactory.getLogger(ChEBIParserService.class);
    private static final String CHEBI_FETCH_URL = "https://www.ebi.ac.uk/webservices/chebi/2.0/test/getCompleteEntity?chebiId=%s";

    public ChEBIParserService(ChEBIIdentifier identifier) {
        getXmlReader().setContentHandler(new ChEBIContentHandler());
        setResource(String.format(CHEBI_FETCH_URL, identifier.getContent()));
    }

    public ChEBIParserService(ChEBIIdentifier identifier, String primaryIdentifier) {
        getXmlReader().setContentHandler(new ChEBIContentHandler(primaryIdentifier));
        setResource(String.format(CHEBI_FETCH_URL, identifier.getContent()));
    }

    public static SmallMolecule parse(String chEBIIdentifier) {
        return ChEBIParserService.parse(new ChEBIIdentifier(chEBIIdentifier));
    }

    public static SmallMolecule parse(String chEBIIdentifier, String primaryIdentifier) {
        logger.info("Parsing chemical entity with identifier " + chEBIIdentifier + " from " + ChEBIDatabase.DEGTYARENKO2008.getIdentifier());
        ChEBIParserService parser = new ChEBIParserService(new ChEBIIdentifier(chEBIIdentifier), primaryIdentifier);
        return parser.parse();
    }

    public static SmallMolecule parse(ChEBIIdentifier identifier) {
        logger.info("Parsing chemical entity with identifier " + identifier.getContent() + " from " + ChEBIDatabase.DEGTYARENKO2008.getIdentifier());
        ChEBIParserService parser = new ChEBIParserService(identifier);
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
    public SmallMolecule parse() {
        parseXML();
        return ((ChEBIContentHandler) getXmlReader().getContentHandler()).getSpecies();
    }


}
