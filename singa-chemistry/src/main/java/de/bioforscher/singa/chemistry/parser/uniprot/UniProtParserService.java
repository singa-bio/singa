package de.bioforscher.singa.chemistry.parser.uniprot;

import de.bioforscher.singa.chemistry.descriptive.Protein;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;
import de.bioforscher.singa.core.parser.xml.AbstractXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
        setResource("http://www.uniprot.org/uniprot/");
        setIdentifier(new UniProtIdentifier(uniProtIdentifier));
    }

    private UniProtParserService(String uniProtIdentifier, String primaryIdentifier) {
        getXmlReader().setContentHandler(new UniProtContentHandler(primaryIdentifier));
        setResource("http://www.uniprot.org/uniprot/");
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

    public void setIdentifier(UniProtIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Protein parse() {
        // create client and target
        fetchResource(this.identifier.toString() + ".xml");
        // parse xml
        try {
            this.getXmlReader().parse(new InputSource(getFetchResult()));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse xml from fetch result, the server seems to be unavailable.", e);
        } catch (SAXException e) {
            e.printStackTrace();
        }
        // return parsing result
        return ((UniProtContentHandler) this.getXmlReader().getContentHandler()).getChemicalSpecies();
    }

}
