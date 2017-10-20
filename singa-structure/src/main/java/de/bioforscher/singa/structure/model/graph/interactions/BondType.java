package de.bioforscher.singa.structure.model.graph.interactions;

import java.util.Arrays;
import java.util.Optional;

public enum BondType {

    SINGLE_BOND("SING"), DOUBLE_BOND("DOUB"), TRIPLE_BOND("TRIP"), HYDROPHOBIC_INTERACTION(""), HYDROGEN_BOND(""), PI_STACKING("");

    private String cifName;

    BondType (String cifName) {
        this.cifName = cifName;
    }

    public String getCifName() {
        return this.cifName;
    }

    public static Optional<BondType> getBondTypeByCifName(String cifName) {
        return Arrays.stream(values())
                .filter(type -> cifName.equals(type.cifName))
                .findAny();
    }

}
