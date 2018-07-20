package bio.singa.core.parser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * This class allows handling and parameter definition for simple HTML requests.
 *
 * @param <ResultType> The result of the parsing.
 * @author cl
 */
public abstract class AbstractHTMLParser<ResultType> extends AbstractParser<ResultType> {

    /**
     * Fetches the currently set resource, by building an URL and opening a stream.
     */
    public void fetchResource() {
        try {
            URL url = new URL(getResource());
            setFetchResult(url.openStream());
        } catch (MalformedURLException e) {
            throw new UncheckedIOException("The url \"" + getResource() + "\" seems to be malformed", e);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not connect to \"" + getResource() + "\", the server seems to be unavailable.", e);
        }
    }

    /**
     * Fetches the currently set resource, concatenating the given extension before opening a stream.
     *
     * @param resourceExtension The string that is added to the end of the current resource.
     */
    public void fetchResource(String resourceExtension) {
        String urlString = getResource() + resourceExtension;
        try {
            URL url = new URL(urlString);
            setFetchResult(url.openStream());
        } catch (MalformedURLException e) {
            throw new UncheckedIOException("The url \"" + urlString + "\" seems to be malformed", e);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not connect to \"" + urlString + "\", the server seems to be unavailable.", e);
        }
    }

    /**
     * Builds a URL with the specified parameters in the query map and sets the input stream as a fetch result.
     *
     * @param queryMap A map with the query, key is the name of the parameter, value is the parameter itself.
     */
    public void fetchWithQuery(Map<String, String> queryMap) {
        // build query
        StringBuilder query = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = queryMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            query.append(entry.getKey())
                    .append('=')
                    .append(entry.getValue());
            // connect request parameters
            if (iterator.hasNext()) {
                query.append('&');
            }
        }
        fetchResource(query.toString());
    }

}
