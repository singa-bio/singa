package bio.singa.core.parser;

/**
 * Implements methods to fetch resource and parse articles into the right format.
 *
 * @param <ResultType> The result of the parsing.
 * @author cl
 */
public interface Parser<ResultType> {

    /**
     * Returns the result of the parsing.
     *
     * @return The result.
     */
    ResultType parse();

}
