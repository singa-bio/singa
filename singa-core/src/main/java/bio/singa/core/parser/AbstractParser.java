package bio.singa.core.parser;

import java.io.InputStream;

/**
 * The class allows definition of resource path and storage for the fetched result.
 *
 * @param <ResultType> The result of the parsing.
 * @author cl
 */
public abstract class AbstractParser<ResultType> implements Parser<ResultType> {

    /**
     * The resource - preferably the plain URL or file path.
     */
    private String resource;

    /**
     * The result of any fetch operation that is to be parsed.
     */
    private InputStream fetchResult;

    /**
     * Returns the resource.
     *
     * @return The resource.
     */
    public String getResource() {
        return resource;
    }

    /**
     * Sets the resource.
     *
     * @param resource The resource.
     */
    protected void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * After fetching the current resource, this returns the fetch result.
     *
     * @return The fetch result.
     */
    public InputStream getFetchResult() {
        return fetchResult;
    }

    /**
     * Sets the result of any fetch operation.
     *
     * @param fetchResult The fetch result.
     */
    protected void setFetchResult(InputStream fetchResult) {
        this.fetchResult = fetchResult;
    }

}
