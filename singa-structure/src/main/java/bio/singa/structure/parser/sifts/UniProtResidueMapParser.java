package bio.singa.structure.parser.sifts;

import bio.singa.core.parser.AbstractXMLParser;
import bio.singa.features.identifiers.PDBIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
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
public class UniProtResidueMapParser extends AbstractXMLParser<Map<UniProtIdentifier, Map<PdbLeafIdentifier, Integer>>> {

    private static final Logger logger = LoggerFactory.getLogger(UniProtResidueMapParser.class);
    private static final String RESIDUE_MAP_FETCH_URL = "http://ftp.ebi.ac.uk/pub/databases/msd/sifts/xml/%s.xml.gz";

    public UniProtResidueMapParser(PDBIdentifier identifier) {
        getXmlReader().setContentHandler(new ResidueMapContentHandler(identifier.toString()));
        setResource(String.format(RESIDUE_MAP_FETCH_URL, identifier.getContent().toLowerCase()));
    }

    public static Map<UniProtIdentifier, Map<PdbLeafIdentifier, Integer>> parse(String pdbIdentifier) {
        return UniProtResidueMapParser.parse(new PDBIdentifier(pdbIdentifier));
    }

    public static Map<UniProtIdentifier, Map<PdbLeafIdentifier, Integer>> parse(PDBIdentifier pdbIdentifier) {
        logger.info("parsing PDB structure with identifier " + pdbIdentifier + " from SIFTS Database");
        UniProtResidueMapParser parser = new UniProtResidueMapParser(pdbIdentifier);
        return parser.parse();
    }

    @Override
    public Map<UniProtIdentifier, Map<PdbLeafIdentifier, Integer>> parse() {
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
