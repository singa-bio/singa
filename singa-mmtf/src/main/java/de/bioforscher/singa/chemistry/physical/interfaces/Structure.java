package de.bioforscher.singa.chemistry.physical.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public interface Structure extends LeafSubstructureContainer, ChainContainer {

    String getPdbIdentifier();

    String getTitle();

    List<Model> getAllModels();

    Model getFirstModel();

    Optional<Model> getModel(int modelIdentifier);

    Optional<Chain> getChain(int modelIdentifier, String chainIdentifier);

    Structure getCopy();

}
