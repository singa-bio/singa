package de.bioforscher.chemistry.parser.uniprot;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.chemistry.descriptive.Protein;
import de.bioforscher.core.identifier.UniProtIdentifier;
import de.bioforscher.core.parser.FetchResultContainer;
import de.bioforscher.core.parser.xml.AbstractXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author cl
 */
public class UniProtParserService extends AbstractXMLParser {

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
        return parser.fetchProtein();
    }

    public static Protein parse(String uniProtIdentifier, String primaryIdentifier) {
        UniProtParserService parser = new UniProtParserService(uniProtIdentifier, primaryIdentifier);
        return parser.fetchProtein();
    }

    public void setIdentifier(UniProtIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public void fetchResource() {
        // create client and target
        Client client = ClientBuilder.newClient();
        WebTarget targetResource = client.target(getResource());
        WebTarget query = targetResource.path(this.identifier.toString() + ".xml");
        // build request
        Invocation.Builder invocationBuilder = query.request(MediaType.TEXT_PLAIN);
        logger.info("Waiting for response.");
        // get response
        Response response = invocationBuilder.get();
        // set result
        setFetchResult(new FetchResultContainer<>(response.readEntity(String.class)));
        // parse xml
        try {
            this.getXmlReader()
                .parse(new InputSource(new ByteArrayInputStream(getFetchResult().getContent().getBytes("utf-8"))));
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public Protein fetchProtein() {
        fetchResource();
        return ((UniProtContentHandler) this.getXmlReader().getContentHandler()).getChemicalSpecies();
    }

    @Override
    public List<Object> parseObjects() {
        return null;
    }

}
