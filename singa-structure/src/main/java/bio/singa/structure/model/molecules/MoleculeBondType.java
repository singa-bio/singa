package bio.singa.structure.model.molecules;

import bio.singa.structure.model.oak.BondType;

import java.util.stream.Stream;

/**
 * @author cl
 */
public enum MoleculeBondType {

    SINGLE_BOND('-', 1),
    DOUBLE_BOND('=', 2),
    TRIPLE_BOND('#', 3),
    QUADRUPLE_BOND('$', 4),
    ISOMERIC_BOND_UP('/', 0),
    ISOMERIC_BOND_DOWN('\\', 0),
    AROMATIC_BOND(':', 0),
    UNCONNECTED('.', 0);
    // '.' is a "non bond"

    private final char smilesRepresentation;
    private final int bondOrder;

    MoleculeBondType(char smilesRepresentation, int bondOrder) {
        this.smilesRepresentation = smilesRepresentation;
        this.bondOrder = bondOrder;
    }

    public static MoleculeBondType getBondForSMILESSymbol(char smilesSymbol) {
        return Stream.of(values())
                .filter(type -> type.getSmilesRepresentation() == smilesSymbol)
                .findAny()
                .orElse(SINGLE_BOND);
    }

    public static MoleculeBondType getBondForOakBondType(BondType bondType) {
        switch (bondType) {
            case SINGLE_BOND:
                return SINGLE_BOND;
            case DOUBLE_BOND:
                return DOUBLE_BOND;
            case TRIPLE_BOND:
                return TRIPLE_BOND;
            default:
                return UNCONNECTED;
        }
    }

    public int getBondOrder() {
        return bondOrder;
    }

    public char getSmilesRepresentation() {
        return smilesRepresentation;
    }
}
