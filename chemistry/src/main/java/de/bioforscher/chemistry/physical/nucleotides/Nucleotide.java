package de.bioforscher.chemistry.physical.nucleotides;

import de.bioforscher.chemistry.physical.model.SubStructure;
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
public class Nucleotide extends SubStructure implements Nameable {
    /**
     * Creates a new SubStructure. The identifier is considered in the superordinate SubStructure.
     *
     * @param identifier The identifier of this SubStructure.
     */
    public Nucleotide(int identifier) {
        super(identifier);
    }

    @Override
    public String getName() {
        return null;
    }
}
