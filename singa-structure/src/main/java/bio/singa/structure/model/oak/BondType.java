package bio.singa.structure.model.oak;

import java.util.Arrays;
import java.util.Optional;

public enum BondType {

    SINGLE_BOND("SING"), DOUBLE_BOND("DOUB"), TRIPLE_BOND("TRIP"), HYDROPHOBIC_INTERACTION(""), HYDROGEN_BOND(""), PI_STACKING("");

    private final String cifName;

    BondType(String cifName) {
        this.cifName = cifName;
    }

    public static Optional<BondType> getBondTypeByCifName(String cifName) {
        return Arrays.stream(values())
                .filter(type -> cifName.equals(type.cifName))
                .findAny();
    }

    public String getCifName() {
        return cifName;
    }

}
