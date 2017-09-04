package de.bioforscher.singa.chemistry.physical.model;

/**
 * A {@link StructuralFamily} defines a PDB-conform label that can at least be expressed in one-letter and/or
 * three-letter code.
 *
 * @author fk
 */
public interface StructuralFamily<FamilyType extends Comparable<FamilyType>> extends Comparable<FamilyType> {

    String getOneLetterCode();

    String getThreeLetterCode();

}
