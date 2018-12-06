package bio.singa.mathematics.vectors;

import java.util.List;

public class BitVectors {

    public static Vector count(List<BitVector> bitVectors) {

        if (bitVectors.stream()
                .map(BitVector::getDimension)
                .distinct()
                .count() != 1) {
            throw new IllegalArgumentException("bit vectors must be of same dimension");
        }

        double[] countElements = new double[bitVectors.get(0).getDimension()];
        for (BitVector bitVector : bitVectors) {
            for (int i = 0; i < bitVector.getElements().length; i++) {
                if (bitVector.getElement(i)) {
                    countElements[i] += 1.0;
                }
            }
        }
        return new RegularVector(countElements);
    }
}
