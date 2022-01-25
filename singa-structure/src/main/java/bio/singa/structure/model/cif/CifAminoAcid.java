package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.AminoAcid;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CifAminoAcid extends CifLeafSubstructure implements AminoAcid {

    private String divergingThreeLetterCode;

    /**
     * string is "pdbx_role" of _struct_conn
     */
    private final Map<String, Set<CifLeafSubstructure>> modifications;

    public CifAminoAcid(CifLeafIdentifier leafIdentifier) {
        super(leafIdentifier);
        modifications = new HashMap<>();
    }

    public CifAminoAcid(CifAminoAcid cifAminoAcid) {
        super(cifAminoAcid);
        divergingThreeLetterCode = cifAminoAcid.divergingThreeLetterCode;
        modifications = new HashMap<>();
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

    public Map<String, Set<CifLeafSubstructure>> getModifications() {
        return modifications;
    }

    @Override
    public CifAminoAcid getCopy() {
        return new CifAminoAcid(this);
    }

}
