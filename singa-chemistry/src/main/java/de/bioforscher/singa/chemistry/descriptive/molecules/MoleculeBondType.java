package de.bioforscher.singa.chemistry.descriptive.molecules;

import java.util.stream.Stream;

public enum MoleculeBondType {

    SINGLE_BOND('-'),
    DOUBLE_BOND('='),
    TRIPLE_BOND('#'),
    QUADRUPLE_BOND('$'),
    ISOMERIC_BOND_UP('/'),
    ISOMERIC_BOND_DOWN('\\'),
    AROMATIC_BOND(':'),
    UNCONNECTED('.');
    // '.' is a "non bond"

    /**
     * @author cl
     */
    private char smilesRepresentation;

    MoleculeBondType(char smilesRepresentation) {
        this.smilesRepresentation = smilesRepresentation;
    }

    public char getSmilesRepresentation() {
        return this.smilesRepresentation;
    }

    public static MoleculeBondType getBondForSMILESSymbol(char smilesSymbol) {
        return Stream.of(values())
                .filter(type -> type.getSmilesRepresentation() == smilesSymbol)
                .findAny()
                .orElse(SINGLE_BOND);
    }
}
