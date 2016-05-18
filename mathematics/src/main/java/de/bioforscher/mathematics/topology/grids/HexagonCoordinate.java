package de.bioforscher.mathematics.topology.grids;

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
     * TODO: redesign to metric implementation
     *
     * @param h1
     * @param h2
     * @return
     */
    public static int getDistance(HexagonCoordinate h1, HexagonCoordinate h2) {
        int x1 = h1.getQ();
        int z1 = h1.getR();
        int x2 = h2.getQ();
        int z2 = h2.getR();
        int y1 = -(x1 + z1);
        int y2 = -(x2 + z2);
        return (Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2)) / 2;
    }

    @Override
    public int hashCode() {
        // curtesy :
        // http://stackoverflow.com/questions/919612/mapping-two-integers-to-one-in-a-unique-and-deterministic-way
        // has to be optimized
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
        if (this.q != other.q) {
            return false;
        }
        return this.r == other.r;
    }

    @Override
    public String toString() {
        return "Hexagon (" + this.q + ", " + this.r + ")";
    }

}
