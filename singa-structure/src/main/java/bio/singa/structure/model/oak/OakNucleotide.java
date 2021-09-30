package bio.singa.structure.model.oak;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Nucleotide;

/**
 * @author cl
 */
public class OakNucleotide extends OakLeafSubstructure implements Nucleotide {

    private boolean mutation;
    private StructuralFamily wildTypeNucleotide;

    public OakNucleotide(PdbLeafIdentifier leafIdentifier, StructuralFamily family) {
        super(leafIdentifier, family);
    }

    public OakNucleotide(PdbLeafIdentifier identifer, StructuralFamily nucleotideFamily, String threeLetterCode) {
        super(identifer, nucleotideFamily, threeLetterCode);
    }

    public OakNucleotide(OakNucleotide oakNucleotide) {
        super(oakNucleotide);
        mutation = oakNucleotide.mutation;
        wildTypeNucleotide = oakNucleotide.wildTypeNucleotide;
    }

    @Override
    public OakNucleotide getCopy() {
        return new OakNucleotide(this);
    }

    public boolean isMutated() {
        return mutation;
    }

    public void setMutation(boolean mutation) {
        this.mutation = mutation;
    }

    public StructuralFamily getWildTypeNucleotide() {
        return wildTypeNucleotide;
    }

    public void setWildTypeNucleotide(StructuralFamily wildTypeNucleotide) {
        this.wildTypeNucleotide = wildTypeNucleotide;
    }

}
