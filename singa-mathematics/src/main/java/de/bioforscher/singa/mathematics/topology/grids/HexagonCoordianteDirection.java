package de.bioforscher.singa.mathematics.topology.grids;

public enum HexagonCoordianteDirection {

    East(0),
    NorthEast(1),
    NorthWest(2),
    West(3),
    SouthWest(4),
    SouthEast(5);

    private final int value;

    HexagonCoordianteDirection(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
