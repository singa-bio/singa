package bio.singa.core.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * This is a clumsy parser for the response provided by the UniProt ID mapping service. Due to redirection and
 * potentially malformed URLs that contains white spaces, this is required. Automatic redirection will not work here and
 * needs manual intervention.
 * <b>Do not use for other purposes.</b>
 *
 * @param <ResultType> The type of the parsing result.
 */
public abstract class AbstractUniProtIdentifierMappingParser<ResultType> extends AbstractHTMLParser<ResultType> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractUniProtIdentifierMappingParser.class);

    @Override
    public void fetchResource(String resourceExtension) {
        String urlString = getResource() + resourceExtension;
        try {
            URL url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            httpUrlConnection.setInstanceFollowRedirects(false);
            int httpResponseCode = httpUrlConnection.getResponseCode();
            boolean redirected = ((httpResponseCode >= 300) && (httpResponseCode <= 399));
            if (redirected) {
                String redirectedURLString = urlConnection.getHeaderField("Location");
                String encodedUrlString = redirectedURLString.replaceAll(" ", "%20");
                URL redirectedUrl;
                if (redirectedURLString.startsWith("http")) {
                    redirectedUrl = new URL(encodedUrlString);
                } else {
                    redirectedUrl = new URL(url, encodedUrlString);
                }
                setFetchResult(redirectedUrl.openStream());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to retrieve UniProt mapping for " + urlString, e);
        }
    }
}
