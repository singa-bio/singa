package de.bioforscher.singa.core.parser.rest;

import de.bioforscher.singa.core.parser.FetchResultContainer;
import de.bioforscher.singa.core.parser.Parser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Implements default method to fetch from a defined resource.
 *
 * @author Christoph Leberecht
 */
public interface RESTParser extends Parser {

    String getResource();

    Map<String, String> getQueryMap();

    void setFetchResult(FetchResultContainer<String> fetchResult);

    /**
     * Builds a {@code Client} and {@code WebTarget} with the specified
     * parameters in the querry map. The result is written with the
     * {@code  setFetchResult(String fetchResult)} method.
     */
    @Override
    default void fetchResource() {

        // create client
        Client client = ClientBuilder.newClient();
        WebTarget targetResource = client.target(getResource());
        Map<String, String> queryMap = getQueryMap();

        // build query
        WebTarget query = null;
        for (String target : queryMap.keySet()) {
            if (query == null) {
                query = targetResource.queryParam(target,
                        queryMap.get(target));
            } else {
                query = query.queryParam(target, queryMap.get(target));
            }
        }
        Invocation.Builder invocationBuilder = query
                .request(MediaType.TEXT_PLAIN);

        // get response
        Response response = invocationBuilder.get();

        // set result
        setFetchResult(new FetchResultContainer<>(response.readEntity(String.class)));

    }

}
