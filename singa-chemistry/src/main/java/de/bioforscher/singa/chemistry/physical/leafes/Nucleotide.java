package de.bioforscher.singa.chemistry.physical.leafes;

import de.bioforscher.singa.chemistry.physical.families.LeafFactory;
import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

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
        throw new UnsupportedOperationException();
    }
}
