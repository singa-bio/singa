package de.bioforscher.singa.chemistry.physical.model;

/**
 * A {@link StructuralFamily} defines a PDB-conform label that can at least be expressed in one-letter and/or
 * three-latter code.
 *
 * @author fk
 */
public interface StructuralFamily<G extends Comparable<G>> extends Comparable<G> {

    String getOneLetterCode();

    String getThreeLetterCode();


}
