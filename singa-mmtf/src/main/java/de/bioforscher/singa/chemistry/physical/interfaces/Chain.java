package de.bioforscher.singa.chemistry.physical.interfaces;

/**
 * @author cl
 */
public interface Chain extends LeafSubstructureContainer {

    String getIdentifier();

    Chain getCopy();

}
