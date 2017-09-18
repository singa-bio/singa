package de.bioforscher.singa.mmtf;

/**
 * @author fk
 */
public interface StructureInterface {

    String getPdbIdentifier();

    String getTitle();

    ChainInterface getChain(String chainIdentifier);

}
