package de.bioforscher.singa.mathematics.topology.grids;

/**
 * @author cl
 */
public class UnitCubeCoordinate {

    private final int x;
    private final int y;
    private final int z;

    public UnitCubeCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
