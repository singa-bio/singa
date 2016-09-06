package de.bioforscher.core.parser;

import java.util.List;

/**
 * Implements methods to fetch resource and parse articles into the right format.
 *
 * @author Christoph Leberecht
 */
public interface Parser {

    /**
     * Fetches the resource.
     */
    public void fetchResource();

    /**
     * Parses the result of {@code fetchResource()}.
     *
     * @return a list of Objects
     */
    public List<Object> parseObjects();

}