package de.bioforscher.chemistry.descriptive.molecules;

import java.util.stream.Stream;

/**
 * Created by Christoph on 21/11/2016.
 */
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
