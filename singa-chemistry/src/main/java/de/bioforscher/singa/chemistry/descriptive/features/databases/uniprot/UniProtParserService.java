package de.bioforscher.singa.chemistry.descriptive.features.databases.uniprot;

import de.bioforscher.singa.chemistry.descriptive.entities.Protein;
import de.bioforscher.singa.core.parser.AbstractXMLParser;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.identifiers.model.Identifier;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
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

    private UniProtIdentifier identifier;

    private UniProtParserService(String uniProtIdentifier) {
        getXmlReader().setContentHandler(new UniProtContentHandler());
        setResource("https://www.uniprot.org/uniprot/");
        setIdentifier(new UniProtIdentifier(uniProtIdentifier));
    }

    private UniProtParserService(String uniProtIdentifier, String primaryIdentifier) {
        getXmlReader().setContentHandler(new UniProtContentHandler(primaryIdentifier));
        setResource("https://www.uniprot.org/uniprot/");
        setIdentifier(new UniProtIdentifier(uniProtIdentifier));
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

    public void setIdentifier(UniProtIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Protein parse() {
        parseXML();
        // return parsing result
        return ((UniProtContentHandler) getXmlReader().getContentHandler()).getProtein();
    }

    private void parseXML() {
        fetchResource(identifier.getIdentifier() + ".xml");
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
