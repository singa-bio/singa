package de.bioforscher.singa.core.parser;

/**
 * The class allows definition of resource path and storage for the fetched result.
 *
 * @author Christoph Leberecht
 */
public abstract class AbstractParser<ContainerType> implements Parser {

    /**
     * The resource - preferably URL or file path
     */
    private String resource;

    /**
     * The result that is to be parsed.
     */
    private FetchResultContainer<ContainerType> fetchResult;

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public FetchResultContainer<ContainerType> getFetchResult() {
        return this.fetchResult;
    }

    public void setFetchResult(FetchResultContainer<ContainerType> fetchResult) {
        this.fetchResult = fetchResult;
    }

}
