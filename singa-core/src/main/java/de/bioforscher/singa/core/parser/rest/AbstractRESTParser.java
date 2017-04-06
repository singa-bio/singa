package de.bioforscher.singa.core.parser.rest;

import de.bioforscher.singa.core.parser.AbstractParser;

import java.util.Map;

/**
 * This class allows handling and parameter definition for simple REST requests.
 *
 * @author Christoph Leberecht
 */
public abstract class AbstractRESTParser extends AbstractParser<String> implements RESTParser {

    /**
     * the query parameter
     */
    private Map<String, String> queryMap;

    public Map<String, String> getQueryMap() {
        return this.queryMap;
    }

    public void setQueryMap(Map<String, String> queryMap) {
        this.queryMap = queryMap;
    }

}
