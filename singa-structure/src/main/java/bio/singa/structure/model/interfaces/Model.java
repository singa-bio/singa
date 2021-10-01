package bio.singa.structure.model.interfaces;

import java.util.Optional;
import java.util.Set;

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
     * Returns A set of all chin identifiers referenced in th model.
     * @return A set of all chin identifiers referenced in th model.
     */
    Set<String> getAllChainIdentifiers();

    /**
     * Returns an {@link Optional} of the {@link Chain} with the given identifier. If no chain with the identifier could
     * be found, an empty optional is returned.
     *
     * @param chainIdentifier The identifier of the chain.
     * @return An {@link Optional} encapsulating the {@link Chain}.
     */
    Optional<Chain> getChain(String chainIdentifier);

    /**
     * Removes a {@link Chain} with the given identifier from the structure.
     *
     * @param chainIdentifier The identifier of the chain.
     */
    void removeChain(String chainIdentifier);

    /**
     * Returns a copy of this model.
     *
     * @return A copy of this model.
     */
    Model getCopy();

    default String flatToString() {
        return getFirstChain().getFirstLeafSubstructure().getIdentifier().getStructureIdentifier()+ "-" + getModelIdentifier();
    }

}
