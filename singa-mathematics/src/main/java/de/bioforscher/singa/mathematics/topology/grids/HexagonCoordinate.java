package de.bioforscher.singa.mathematics.topology.grids;

public class HexagonCoordinate {

    private final short q;
    private final short r;

    public HexagonCoordinate(short q, short r) {
        this.q = q;
        this.r = r;
    }

    public HexagonCoordinate(int q, int r) {
        this((short) q, (short) r);
    }

    public short getQ() {
        return this.q;
    }

    public short getR() {
        return this.r;
    }

    /**
     * Returns the distance between two coordinates.
     *
     * @param firstCoordinate The first coordinate
     * @param secondCoordinate The second coordinate.
     * @return The distance between two coordinates.
     */
    public static int getDistance(HexagonCoordinate firstCoordinate, HexagonCoordinate secondCoordinate) {
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
        long A = (long) (this.q >= 0 ? 2 * this.q : -2 * this.q - 1);
        long B = (long) (this.r >= 0 ? 2 * this.r : -2 * this.r - 1);
        int C = (int) ((A >= B ? A * A + A + B : A + B * B) / 2);
        return this.q < 0 && this.r < 0 || this.q >= 0 && this.r >= 0 ? C : -C - 1;
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
        HexagonCoordinate other = (HexagonCoordinate) obj;
        return this.q == other.q && this.r == other.r;
    }

    @Override
    public String toString() {
        return "Hexagon (" + this.q + ", " + this.r + ")";
    }

}
