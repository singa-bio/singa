package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.families.NucleotideFamily;

import java.util.EnumMap;

/**
 * A nucleotide is a grouping element that should only contain atoms. Each and every residue has a associate NucleotideType,
 * that determines the nucleotide. Based on this NucleotideType a Nucleotide can be created
 * from a set of atoms that belong to this residue using the
 * {@link LeafFactory#createNucleotideFromAtoms(int, NucleotideFamily, EnumMap) NucleotideFactory}. This establishes the bonds
 * within the Nucleotides, where possible.
 *
 *
 * @author fkaiser
 */
public class Nucleotide extends LeafSubstructure<Nucleotide,NucleotideFamily>{

    private NucleotideFamily family;

    public Nucleotide(int identifier) {
        super(identifier);
    }

    public Nucleotide(int identifier, NucleotideFamily family) {
        super(identifier);
        this.family = family;
    }

    public Nucleotide(Nucleotide nucleotide) {
        super(nucleotide);
        this.family = nucleotide.family;
    }

    @Override
    public Nucleotide getCopy() {
        return new Nucleotide(this);
    }

    @Override
    public NucleotideFamily getFamily() {
        return this.family;
    }
}
