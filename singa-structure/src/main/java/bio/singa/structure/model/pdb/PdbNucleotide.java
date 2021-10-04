package bio.singa.structure.model.pdb;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Nucleotide;

/**
 * @author cl
 */
public class PdbNucleotide extends PdbLeafSubstructure implements Nucleotide {

    private boolean mutation;
    private StructuralFamily wildTypeNucleotide;

    public PdbNucleotide(PdbLeafIdentifier leafIdentifier, StructuralFamily family) {
        super(leafIdentifier, family);
    }

    public PdbNucleotide(PdbLeafIdentifier identifer, StructuralFamily nucleotideFamily, String threeLetterCode) {
        super(identifer, nucleotideFamily, threeLetterCode);
    }

    public PdbNucleotide(PdbNucleotide oakNucleotide) {
        super(oakNucleotide);
        mutation = oakNucleotide.mutation;
        wildTypeNucleotide = oakNucleotide.wildTypeNucleotide;
    }

    @Override
    public PdbNucleotide getCopy() {
        return new PdbNucleotide(this);
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
