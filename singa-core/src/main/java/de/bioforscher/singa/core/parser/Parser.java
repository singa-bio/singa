package de.bioforscher.singa.core.parser;

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
    void fetchResource();

    /**
     * Parses the result of {@code fetchResource()}.
     *
     * @return a list of Objects
     */
    List<Object> parseObjects();

}
