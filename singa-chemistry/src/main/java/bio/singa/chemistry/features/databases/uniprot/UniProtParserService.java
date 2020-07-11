package bio.singa.chemistry.features.databases.uniprot;

import bio.singa.chemistry.model.Protein;
import bio.singa.core.parser.AbstractXMLParser;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.identifiers.model.IdentifierPatternRegistry;
import bio.singa.features.quantities.MolarMass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.measure.Quantity;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author cl
 */
public class UniProtParserService extends AbstractXMLParser<Protein> {

    private static final Logger logger = LoggerFactory.getLogger(UniProtParserService.class);

    private Identifier identifier;

    private UniProtParserService(String uniProtIdentifier) {
        getXmlReader().setContentHandler(new UniProtContentHandler());
        setResource("https://www.uniprot.org/uniprot/");
        setIdentifier(uniProtIdentifier);
    }

    private UniProtParserService(String uniProtIdentifier, String primaryIdentifier) {
        getXmlReader().setContentHandler(new UniProtContentHandler(primaryIdentifier));
        setResource("https://www.uniprot.org/uniprot/");
        setIdentifier(uniProtIdentifier);
    }

    public static Protein parse(String uniProtIdentifier) {
        UniProtParserService parser = new UniProtParserService(uniProtIdentifier);
        return parser.parse();
    }

    public static Protein parse(String uniProtIdentifier, String primaryIdentifier) {
        UniProtParserService parser = new UniProtParserService(uniProtIdentifier, primaryIdentifier);
        return parser.parse();
    }

    public static Quantity<MolarMass> fetchMolarMass(Identifier uniProtIdentifier) {
        UniProtParserService parser = new UniProtParserService(uniProtIdentifier.toString());
        parser.parseXML();
        return ((UniProtContentHandler) parser.getXmlReader().getContentHandler()).getMass();
    }

    public void setIdentifier(String identifier) {
        this.identifier = IdentifierPatternRegistry.instantiate(identifier)
                .orElseThrow(() -> new IllegalArgumentException(identifier + "is neither an UniProt name nor identifier."));

    }

    @Override
    public Protein parse() {
        parseXML();
        // return parsing result
        return ((UniProtContentHandler) getXmlReader().getContentHandler()).getProtein();
    }

    private void parseXML() {
        fetchResource(identifier.getContent() + ".xml");
        // parse xml
        try {
            getXmlReader().parse(new InputSource(getFetchResult()));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse XML from fetch result, the server seems to be unavailable.", e);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
