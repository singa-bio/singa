package de.bioforscher.singa.structure.model.interfaces;

/**
 * {@link Chain}s that represent one continuous macro molecule (most often the biggest macro molecules and its ligands
 * are collected in one model). Chains contain {@link LeafSubstructure}s that in tun contain {@link Atom}s.
 *
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
