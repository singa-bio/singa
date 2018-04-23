package de.bioforscher.singa.structure.model.molecules;

import java.util.stream.Stream;

/**
 * @author cl
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

    private final char smilesRepresentation;

    MoleculeBondType(char smilesRepresentation) {
        this.smilesRepresentation = smilesRepresentation;
    }

    public static MoleculeBondType getBondForSMILESSymbol(char smilesSymbol) {
        return Stream.of(values())
                .filter(type -> type.getSmilesRepresentation() == smilesSymbol)
                .findAny()
                .orElse(SINGLE_BOND);
    }

    public char getSmilesRepresentation() {
        return smilesRepresentation;
    }
}
