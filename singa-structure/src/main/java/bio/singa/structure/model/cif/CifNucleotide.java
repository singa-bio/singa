package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.Nucleotide;
import bio.singa.structure.model.general.AuthLeafIdentifier;

public class CifNucleotide extends CifLeafSubstructure implements Nucleotide {

    private String divergingThreeLetterCode;

    public CifNucleotide(AuthLeafIdentifier leafIdentifier) {
        super(leafIdentifier);
    }

    public CifNucleotide(CifNucleotide cifNucleotide) {
        super(cifNucleotide);
    }

    @Override
    public String getThreeLetterCode() {
        if (divergingThreeLetterCode != null) {
            return divergingThreeLetterCode;
        }
        return super.getThreeLetterCode();
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
