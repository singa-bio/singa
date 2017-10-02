package de.bioforscher.singa.chemistry.physical.leaves;

import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;

/**
 * A nucleotide is a grouping element that should only contain atoms. Each and every residue has a associate
 * NucleotideType, that determines the nucleotide.
 *
 * @author fk
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

    public boolean isModified() {
        return this.modified;
    }

}
