package de.bioforscher.singa.core.parser;

import java.io.InputStream;

/**
 * The class allows definition of resource path and storage for the fetched result.
 *
 * @author cl
 */
public abstract class AbstractParser<ResultType> implements Parser<ResultType> {

    /**
     * The resource - preferably URL or file path.
     */
    private String resource;

    /**
     * The result that is to be parsed.
     */
    private InputStream fetchResult;

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public InputStream getFetchResult() {
        return this.fetchResult;
    }

    public void setFetchResult(InputStream fetchResult) {
        this.fetchResult = fetchResult;
    }

}
