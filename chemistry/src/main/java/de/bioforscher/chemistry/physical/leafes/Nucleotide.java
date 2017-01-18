package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.mathematics.vectors.Vector3D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.EnumMap;

/**
 * A nucleotide is a grouping element that should only contain atoms. Each and every residue has a associate NucleotideType,
 * that determines the nucleotide. Based on this NucleotideType a Nucleotide can be created
 * from a set of atoms that belong to this residue using the
 * {@link LeafFactory#createNucleotideFromAtoms(int, NucleotideFamily, EnumMap) NucleotideFactory}. This establishes the bonds
 * within the Nucleotides, where possible.
 *
 * @author fkaiser
 */
public class Nucleotide extends LeafSubstructure<Nucleotide, NucleotideFamily> {

    private String name;

    public Nucleotide(LeafIdentifier leafIdentifier, NucleotideFamily family) {
        super(leafIdentifier, family);
        this.name = family.getThreeLetterCode();
    }

    public Nucleotide(Nucleotide nucleotide) {
        super(nucleotide);
        this.name = getFamily().getThreeLetterCode();
    }

    /**
     * Return the name of this {@link Nucleotide} in the format
     * [Chain identifier of the {@link Nucleotide}]-[One Letter Code of the {@link Nucleotide}][AminoAcid identifier]
     * (e.g. A-A123 or A-U17).
     *
     * @return The String representation of this {@link Nucleotide}.
     */
    @Override
    public String toString() {
        return getLeafIdentifier().getChainIdentifer() + "-" + getFamily().getOneLetterCode() + getIdentifier();
    }

    @Override
    public Nucleotide getCopy() {
        return new Nucleotide(this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPosition(Vector3D position) {
        //FIXME not yet implemented
        throw new NotImplementedException();
    }
}
