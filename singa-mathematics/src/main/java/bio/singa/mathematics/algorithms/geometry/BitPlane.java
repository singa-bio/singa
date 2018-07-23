package bio.singa.mathematics.algorithms.geometry;

import java.util.BitSet;

/**
 * @author cl
 */
public class BitPlane {

    private final int width;
    private final int height;
    private BitSet[] plane;

    public BitPlane(int width, int height) {
        this.width = width;
        this.height = height;
        plane = new BitSet[width];
        for (int x = 0; x < plane.length; x++) {
            plane[x] = new BitSet(height);
        }
    }

    public void setBit(int x, int y) {
        plane[x].set(y);
    }

    public boolean getBit(int x, int y) {
        return plane[x].get(y);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
