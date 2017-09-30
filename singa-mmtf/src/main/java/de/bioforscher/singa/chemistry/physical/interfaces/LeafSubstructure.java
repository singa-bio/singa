package de.bioforscher.singa.chemistry.physical.interfaces;

import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;

/**
 * A leaf substructure is a collection of atoms with meta data assigned to it. A leaf substructure can be part of a
 * macro molecule, such as amino acid residues or nucleotides, or independent small molecules.
 *
 * @author cl
 */
public interface LeafSubstructure extends AtomContainer {

    /**
     * Returns the complete leaf identifier. The leaf identifier consists of the PDB identifer, the model identifier,
     * the chain identifier, the serial of the leaf substructure and optionally an insertion code.
     *
     * @return The leaf identifier.
     * @see LeafIdentifier
     */
    LeafIdentifier getIdentifier();

    /**
     * Returns the three letter code of the chemical compound represented by this leaf substructure.
     * @return The three letter code of the chemical compound represented by this leaf substructure.
     */
    String getThreeLetterCode();

    /**
     * Returns a copy of this leaf substructure.
     * @return A copy of this leaf substructure.
     */
    LeafSubstructure getCopy();

    /**
     * Returns the flat String representation of this leaf substructure in the form:
     * <pre>
     *     [LeafFamily] ([ThreeLetterCode]) [LeafIdentifier]
     * </pre>
     * For example:
     * <pre>
     *     Nucleotide (C) 1C0A-1-B-631
     *     AminoAcid (ARG) 1C0A-1-A-78
     *     Ligand (HOH) 1C0A-1-A-1048
     * </pre>
     * @return The flat String representation of this leaf substructure.
     */
    default String flatToString() {
        return getClass().getSimpleName() + " (" + getThreeLetterCode() + ") " + getIdentifier();
    }

}
