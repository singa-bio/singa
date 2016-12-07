package de.bioforscher.chemistry.parser;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.core.identifier.UniProtIdentifier;
import de.bioforscher.core.parser.FetchResultContainer;
import de.bioforscher.core.parser.xml.AbstractXMLParser;
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
 * Created by Christoph on 10.09.2016.
 */
public class UniProtParserService extends AbstractXMLParser {

    private static final UniProtParserService INSTANCE = new UniProtParserService();

    private UniProtIdentifier identifier;

    public UniProtParserService() {
        getXmlReader().setContentHandler(new UniProtContentHandler());
        setResource("http://www.uniprot.org/uniprot/");
    }

    public static Enzyme parse(String uniProtIdentifier) {
        INSTANCE.setIdentifier(new UniProtIdentifier(uniProtIdentifier));
        return INSTANCE.fetchChemicalEntity();
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

    public Enzyme fetchChemicalEntity() {
        fetchResource();
        return ((UniProtContentHandler) this.getXmlReader().getContentHandler()).getChemicalSpecies();
    }

    @Override
    public List<Object> parseObjects() {
        return null;
    }

}
