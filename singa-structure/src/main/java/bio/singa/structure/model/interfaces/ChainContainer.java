package bio.singa.structure.model.interfaces;

import java.util.List;

/**
 * Everything that contains {@link Chain}s.
 *
 * @author cl
 */
public interface ChainContainer {

    /**
     * Returns all {@link Chain}s.
     *
     * @return All {@link Chain}s.
     */
    List<Chain> getAllChains();

    /**
     * Returns the first {@link Chain}.
     *
     * @return The first {@link Chain}.
     */
    Chain getFirstChain();

}
