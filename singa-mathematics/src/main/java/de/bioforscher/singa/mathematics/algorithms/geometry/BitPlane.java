package de.bioforscher.singa.mathematics.algorithms.geometry;

import java.util.BitSet;

/**
 * @author cl
 */
public class BitPlane {

    private BitSet[] plane;

    public BitPlane(int width, int height) {
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

    public void reset() {
        for (BitSet bitSet : plane) {
            bitSet.clear();
        }
    }

}
