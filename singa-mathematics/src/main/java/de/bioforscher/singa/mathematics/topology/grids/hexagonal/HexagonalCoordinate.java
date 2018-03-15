package de.bioforscher.singa.mathematics.topology.grids.hexagonal;

import de.bioforscher.singa.mathematics.topology.model.DiscreteCoordinate;

public class HexagonalCoordinate implements DiscreteCoordinate<HexagonalCoordinate, HexagonalDirection> {

    private final int q;
    private final int r;

    public HexagonalCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    @Override
    public HexagonalCoordinate getNeighbour(HexagonalDirection hexagonalDirection) {
        switch (hexagonalDirection) {
            case EAST:
                return new HexagonalCoordinate(q + 1, r);
            case NORTH_EAST:
                return new HexagonalCoordinate(q + 1, r - 1);
            case NORTH_WEST:
                return new HexagonalCoordinate(q, r - 1);
            case WEST:
                return new HexagonalCoordinate(q - 1, r);
            case SOUTH_WEST:
                return new HexagonalCoordinate(q - 1, r + 1);
            case SOUTH_EAST:
                return new HexagonalCoordinate(q, r + 1);
            default:
                throw new IllegalStateException("The direction " + hexagonalDirection + " is invalid for this coordinate type");
        }
    }

    @Override
    public HexagonalDirection[] getAllDirections() {
        return HexagonalDirection.values();
    }


    /**
     * Returns the distance between two coordinates.
     *
     * @param firstCoordinate The first coordinate
     * @param secondCoordinate The second coordinate.
     * @return The distance between two coordinates.
     */
    public static int getDistance(HexagonalCoordinate firstCoordinate, HexagonalCoordinate secondCoordinate) {
        // TODO: redesign to metric implementation
        int x1 = firstCoordinate.getQ();
        int z1 = firstCoordinate.getR();
        int x2 = secondCoordinate.getQ();
        int z2 = secondCoordinate.getR();
        int y1 = -(x1 + z1);
        int y2 = -(x2 + z2);
        return (Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2)) / 2;
    }

    @Override
    public int hashCode() {
        // courtesy :
        // http://stackoverflow.com/questions/919612/mapping-two-integers-to-one-in-a-unique-and-deterministic-way
        long A = (long) (q >= 0 ? 2 * q : -2 * q - 1);
        long B = (long) (r >= 0 ? 2 * r : -2 * r - 1);
        int C = (int) ((A >= B ? A * A + A + B : A + B * B) / 2);
        return q < 0 && r < 0 || q >= 0 && r >= 0 ? C : -C - 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        HexagonalCoordinate other = (HexagonalCoordinate) obj;
        return q == other.q && r == other.r;
    }

    @Override
    public String toString() {
        return "Hexagon (" + q + ", " + r + ")";
    }

}
