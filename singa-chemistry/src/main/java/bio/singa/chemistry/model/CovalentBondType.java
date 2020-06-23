package bio.singa.chemistry.model;

import java.util.stream.Stream;

/**
 * @author cl
 */
public enum CovalentBondType {

    SINGLE_BOND('-', "SING", 1),
    DOUBLE_BOND('=', "DOUB", 2),
    TRIPLE_BOND('#', "TRIP", 3),
    QUADRUPLE_BOND('$', "", 4),
    ISOMERIC_BOND_UP('/', "", 0),
    ISOMERIC_BOND_DOWN('\\',"", 0),
    AROMATIC_BOND(':',"", 0),
    UNCONNECTED('.',"", 0);

    private final char smilesRepresentation;
    private final String cifRepresentation;
    private final int bondOrder;

    CovalentBondType(char smilesRepresentation, String cifRepresentation, int bondOrder) {
        this.smilesRepresentation = smilesRepresentation;
        this.cifRepresentation = cifRepresentation;
        this.bondOrder = bondOrder;
    }

    public char getSmilesRepresentation() {
        return smilesRepresentation;
    }

    public String getCifRepresentation() {
        return cifRepresentation;
    }

    public int getBondOrder() {
        return bondOrder;
    }

    public static CovalentBondType getBondForSMILESSymbol(char smilesSymbol) {
        return Stream.of(values())
                .filter(type -> type.getSmilesRepresentation() == smilesSymbol)
                .findAny()
                .orElse(SINGLE_BOND);
    }

    public static CovalentBondType getBondForCifString(String cifString) {
        return Stream.of(values())
                .filter(type -> type.getCifRepresentation().equals(cifString))
                .findAny()
                .orElse(SINGLE_BOND);
    }

}
