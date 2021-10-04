package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.Nucleotide;

public class CifNucleotide extends CifLeafSubstructure implements Nucleotide {

    private String divergingThreeLetterCode;

    public CifNucleotide(CifLeafIdentifier leafIdentifier) {
        super(leafIdentifier);
    }

    public CifNucleotide(CifNucleotide cifNucleotide) {
        super(cifNucleotide);
    }

    public String getDivergingThreeLetterCode() {
        return divergingThreeLetterCode;
    }

    public void setDivergingThreeLetterCode(String divergingThreeLetterCode) {
        this.divergingThreeLetterCode = divergingThreeLetterCode;
    }

    @Override
    public CifNucleotide getCopy() {
        return new CifNucleotide(this);
    }

}
