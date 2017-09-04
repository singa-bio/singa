package de.bioforscher.singa.core.parser;

/**
 * Implements methods to fetch resource and parse articles into the right format.
 *
 * @author cl
 */
public interface Parser<ResultType> {

    /**
     * Returns the result of the parsing.
     * @return The result.
     */
    ResultType parse();

}
