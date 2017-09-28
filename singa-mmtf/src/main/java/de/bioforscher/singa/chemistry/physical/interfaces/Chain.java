package de.bioforscher.singa.chemistry.physical.interfaces;

/**
 * @author cl
 */
public interface Chain extends LeafSubstructureContainer {

    /**
     * Returns the chain identifier, a short sequence of alphabetic characters.
     *
     * @return The chain identifier.
     */
    String getIdentifier();

    /**
     * Returns a copy of this chain.
     *
     * @return A copy of this chain.
     */
    Chain getCopy();

}
