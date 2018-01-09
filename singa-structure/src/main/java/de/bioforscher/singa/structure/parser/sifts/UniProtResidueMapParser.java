package de.bioforscher.singa.structure.parser.sifts;

import de.bioforscher.singa.core.parser.AbstractXMLParser;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.identifiers.PDBIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author cl
 */
public class UniProtResidueMapParser extends AbstractXMLParser<Map<LeafIdentifier, Integer>> {

    private static final Logger logger = LoggerFactory.getLogger(UniProtResidueMapParser.class);
    private static final String RESIDUE_MAP_FETCH_URL = "ftp://ftp.ebi.ac.uk/pub/databases/msd/sifts/xml/%s.xml.gz";

    public UniProtResidueMapParser(PDBIdentifier identifier) {
        getXmlReader().setContentHandler(new ResidueMapContentHandler(identifier.toString()));
        setResource(String.format(RESIDUE_MAP_FETCH_URL, identifier));
    }

    public static Map<LeafIdentifier, Integer> parse(String pdbIdentifier) {
        return UniProtResidueMapParser.parse(new PDBIdentifier(pdbIdentifier));
    }

    public static Map<LeafIdentifier, Integer> parse(PDBIdentifier pdbIdentifier) {
        logger.info("Parsing chemical entity with identifier " + pdbIdentifier + " from SIFTS Database");
        UniProtResidueMapParser parser = new UniProtResidueMapParser(pdbIdentifier);
        return parser.parse();
    }

    @Override
    public Map<LeafIdentifier, Integer> parse() {
        parseXML();
        return ((ResidueMapContentHandler) getXmlReader().getContentHandler()).getMapping();
    }

    private void parseXML() {
        fetchResource();
        // parse xml
        try {
            getXmlReader().parse(new InputSource(new GZIPInputStream(getFetchResult())));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse xml from fetch result, the server seems to be unavailable.", e);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
