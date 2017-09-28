package de.bioforscher.singa.chemistry.physical.interfaces;

import java.util.Optional;

/**
 * @author cl
 */
public interface Model extends LeafSubstructureContainer, ChainContainer {

    int getIdentifier();

    Optional<Chain> getChain(String chainIdentifier);

    Model getCopy();

}
