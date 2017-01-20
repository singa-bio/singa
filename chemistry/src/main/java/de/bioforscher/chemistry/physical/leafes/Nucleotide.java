package de.bioforscher.chemistry.physical.leafes;

import de.bioforscher.chemistry.physical.families.LeafFactory;
import de.bioforscher.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.mathematics.vectors.Vector3D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.EnumMap;
import java.util.Map;

/**
 * A nucleotide is a grouping element that should only contain atoms. Each and every residue has a associate NucleotideType,
 * that determines the nucleotide. Based on this NucleotideType a Nucleotide can be created
 * from a set of atoms that belong to this residue using the
 * {@link LeafFactory#createNucleotideFromAtoms(LeafIdentifier, NucleotideFamily, Map)}  NucleotideFactory}. This establishes the bonds
 * within the Nucleotides, where possible.
 *
 * @author fkaiser
 */
public class Nucleotide extends LeafSubstructure<Nucleotide, NucleotideFamily> {

    private final boolean modified;
    private final String modifiedName;

    public Nucleotide(LeafIdentifier leafIdentifier, NucleotideFamily family) {
        super(leafIdentifier, family);
        this.modified = false;
        this.modifiedName = null;
    }

    public Nucleotide(LeafIdentifier leafIdentifier, NucleotideFamily family, String modifiedName) {
        super(leafIdentifier, family);
        this.modified = true;
        this.modifiedName = modifiedName;
    }

    public Nucleotide(Nucleotide nucleotide) {
        super(nucleotide);
        this.modified = nucleotide.modified;
        this.modifiedName = nucleotide.modifiedName;
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
        return this.modified ? this.modifiedName : getFamily().getThreeLetterCode();
    }

    public boolean isModified() {
        return this.modified;
    }

    @Override
    public void setPosition(Vector3D position) {
        //FIXME not yet implemented
        throw new NotImplementedException();
    }
}
