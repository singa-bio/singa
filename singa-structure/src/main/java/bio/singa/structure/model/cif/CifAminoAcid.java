package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.AminoAcid;

public class CifAminoAcid extends CifLeafSubstructure implements AminoAcid {

    private String divergingThreeLetterCode;

    public CifAminoAcid(CifLeafIdentifier leafIdentifier) {
        super(leafIdentifier);
    }

    public CifAminoAcid(CifAminoAcid cifAminoAcid) {
        super(cifAminoAcid);
    }

    public String getDivergingThreeLetterCode() {
        return divergingThreeLetterCode;
    }

    public void setDivergingThreeLetterCode(String divergingThreeLetterCode) {
        this.divergingThreeLetterCode = divergingThreeLetterCode;
    }

    @Override
    public CifAminoAcid getCopy() {
        return new CifAminoAcid(this);
    }

}
