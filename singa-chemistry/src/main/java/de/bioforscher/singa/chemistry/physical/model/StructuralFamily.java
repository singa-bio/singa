package de.bioforscher.singa.chemistry.physical.model;

/**
 * A {@link de.bioforscher.singa.chemistry.physical.model.StructuralFamily} defines a PDB-conform label that can at least be expressed in one-letter and/or
 * three-latter code.
 *
 * @author fk
 */
public interface StructuralFamily<StructuralFamily extends Comparable<StructuralFamily>> extends Comparable<StructuralFamily> {

    String getOneLetterCode();

    String getThreeLetterCode();


}
