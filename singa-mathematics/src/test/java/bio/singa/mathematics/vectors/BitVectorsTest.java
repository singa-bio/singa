package bio.singa.mathematics.vectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BitVectorsTest {

    private static BitVector firstBitVector;
    private static BitVector secondBitVector;

    @BeforeAll
    static void setUp() {
        firstBitVector = new RegularBitVector(true, false, true, true, false, true);
        secondBitVector = new RegularBitVector(true, true, false, true, false, false);
    }

    @Test
    void count() {
        Vector count = BitVectors.count(Stream.of(firstBitVector, secondBitVector)
                .collect(Collectors.toList()));
        assertArrayEquals(new double[]{2.0, 1.0, 1.0, 2.0, 0.0, 1.0}, count.getElements());
    }
}