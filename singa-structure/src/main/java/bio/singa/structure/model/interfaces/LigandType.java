package bio.singa.structure.model.interfaces;

import java.util.Arrays;
import java.util.Optional;

/**
 * http://mmcif.rcsb.org/dictionaries/mmcif_pdbx_v50.dic/Items/_struct_asym.pdbx_type.html
 */
public enum LigandType {

    NUCLEIC_ACID("ATOMN"),
    PROTEIN("ATOMP"),
    SUGAR("ATOMS"),
    COENZYME("HETAC"),
    DRUG("HETAD"),
    ION("HETAI"),
    INHIBITOR("HETAIN"),
    SOLVENT("HETAS"),
    ION_COMPLEX("HETIC"),
    UNKNOWN("");

    private final String pdbxTerm;

    LigandType(String pdbxTerm) {
        this.pdbxTerm = pdbxTerm;
    }

    public String getPdbxTerm() {
        return pdbxTerm;
    }

    public static LigandType getLigandTypeByPdbxTerm(String pdbxTerm) {
        return Arrays.stream(values()).sequential()
                .filter(value -> value.getPdbxTerm().equals(pdbxTerm))
                .findAny()
                .orElse(UNKNOWN);
    }

}
