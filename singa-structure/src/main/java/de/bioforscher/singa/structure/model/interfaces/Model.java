package de.bioforscher.singa.structure.model.interfaces;

import java.util.Optional;

/**
 * Models represent macro molecular structures. Models are most often grouped in Structures where they are different
 * dynamic states of the same molecule.
 *
 * @author cl
 */
public interface Model extends LeafSubstructureContainer, ChainContainer {

    /**
     * Returns the model identifier, an integer greater or equal to 1.
     *
     * @return The model identifier.
     */
    Integer getModelIdentifier();

    /**
     * Returns an {@link Optional} of the {@link Chain} with the given identifier. If no chain with the identifier could
     * be found, an empty optional is returned.
     *
     * @param chainIdentifier The identifier of the chain.
     * @return An {@link Optional} encapsulating the {@link Chain}.
     */
    Optional<Chain> getChain(String chainIdentifier);

    /**
     * Returns a copy of this model.
     *
     * @return A copy of this model.
     */
    Model getCopy();

}
