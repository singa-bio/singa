package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.core.parser.AbstractXMLParser;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ChEBIParserService extends AbstractXMLParser<Species> {

    private static final Logger logger = LoggerFactory.getLogger(ChEBIParserService.class);
    private static final String CHEBI_FETCH_URL = "https://www.ebi.ac.uk/webservices/chebi/2.0/test/getCompleteEntity?chebiId=%s";

    public ChEBIParserService(ChEBIIdentifier identifier) {
        getXmlReader().setContentHandler(new ChEBIContentHandler());
        setResource(String.format(CHEBI_FETCH_URL, identifier.toString()));
    }

    public ChEBIParserService(ChEBIIdentifier identifier, String primaryIdentifier) {
        getXmlReader().setContentHandler(new ChEBIContentHandler(primaryIdentifier));
        setResource(String.format(CHEBI_FETCH_URL, identifier.toString()));
    }

    public static Species parse(String chEBIIdentifier) {
        return ChEBIParserService.parse(new ChEBIIdentifier(chEBIIdentifier));
    }

    public static Species parse(String chEBIIdentifier, String primaryIdentifier) {
        logger.info("Parsing chemical entity with identifier " + chEBIIdentifier + " from " + ChEBIDatabase.origin.getName());
        ChEBIParserService parser = new ChEBIParserService(new ChEBIIdentifier(chEBIIdentifier), primaryIdentifier);
        return parser.parse();
    }


    public static Species parse(ChEBIIdentifier identifier) {
        logger.info("Parsing chemical entity with identifier " + identifier + " from " + ChEBIDatabase.origin.getName());
        ChEBIParserService parser = new ChEBIParserService(identifier);
        return parser.parse();
    }

    public static void main(String[] args) {
        final Species species = ChEBIParserService.parse("CHEBI:17790");
        System.out.println(species);
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
    public Species parse() {
        parseXML();
        return ((ChEBIContentHandler) getXmlReader().getContentHandler()).getSpecies();
    }


}
