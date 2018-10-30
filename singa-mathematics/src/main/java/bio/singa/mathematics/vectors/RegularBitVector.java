package bio.singa.mathematics.vectors;

import bio.singa.mathematics.metrics.model.Metric;

import java.util.Arrays;

public class RegularBitVector implements BitVector {

    private final boolean[] elements;
    private final int dimension;

    public RegularBitVector(boolean... elements) {
        this.elements = elements;
        dimension = elements.length;
    }

    public RegularBitVector(int dimension) {
        elements = new boolean[dimension];
        this.dimension = dimension;
    }

    @Override
    public boolean hasSameDimensions(BitVector bitVector) {
        return bitVector.getDimension() == dimension;
    }

    @Override
    public String getDimensionAsString() {
        return null;
    }

    @Override
    public double distanceTo(BitVector another) {
        return 0;
    }

    @Override
    public double distanceTo(BitVector another, Metric<BitVector> metric) {
        return 0;
    }

    @Override
    public boolean getElement(int index) {
        return elements[index];
    }

    @Override
    public boolean[] getElements() {
        return elements;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        return "BitVector " + getDimension() + "D "
                + Arrays.toString(elements).replace("[", "(").replace("]", ")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegularBitVector that = (RegularBitVector) o;

        if (dimension != that.dimension) return false;
        return Arrays.equals(elements, that.elements);
    }
}
