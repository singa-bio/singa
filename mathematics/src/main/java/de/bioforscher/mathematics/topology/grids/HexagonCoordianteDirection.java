package de.bioforscher.mathematics.topology.grids;

public enum HexagonCoordianteDirection {

    East(0),
    NorthEast(1),
    NothWest(2),
    West(3),
    SouthWest(4),
    SouthEast(5);

    private final int value;

    private HexagonCoordianteDirection(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
