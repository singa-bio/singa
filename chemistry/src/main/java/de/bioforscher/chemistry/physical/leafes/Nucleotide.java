package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.core.utility.Nameable;

import java.util.EnumMap;

/**
 * A nucleotide is a grouping element that should only contain atoms. Each and every residue has a associate NucleotideType,
 * that determines the nucleotide. Based on this NucleotideType a Nucleotide can be created
 * from a set of atoms that belong to this residue using the
 * {@link NucleotideFactory#createNucleotideFromAtoms(int, NucleotideType, EnumMap) NucleotideFactory}. This establishes the bonds
 * within the Nucleotides, where possible.
 *
 * TODO: this is a stub and not yet implemented
 *
 * @author fkaiser
 */
public class Nucleotide extends BranchSubstructure implements Nameable {
    /**
     * Creates a new BranchSubstructure. The identifier is considered in the superordinate BranchSubstructure.
     *
     * @param identifier The identifier of this BranchSubstructure.
     */
    public Nucleotide(int identifier) {
        super(identifier);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public BranchSubstructure getCopy() {
        return null;
    }

}
