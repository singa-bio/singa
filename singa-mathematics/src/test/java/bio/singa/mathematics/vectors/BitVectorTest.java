package bio.singa.mathematics.vectors;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitVectorTest {

    private static RegularBitVector bitVector;
    private static BitVector firstBitVector;
    private static BitVector secondBitVector;

    @BeforeAll
    static void initialize() {
        bitVector = new RegularBitVector(true, false, true, true, false, true);
        firstBitVector = new RegularBitVector(true, false, true, true, false, true);
        secondBitVector = new RegularBitVector(true, true, false, true, false, false);
    }

    @Test
    void getCopy() {
        BitVector copyOfBitVector = bitVector.getCopy();
        copyOfBitVector.getElements()[0] = false;
        assertTrue(bitVector.getElements()[0] != copyOfBitVector.getElements()[0]);
    }

    @Test
    void equals() {
        BitVector copyOfBitVector = bitVector.getCopy();
        assertEquals(bitVector, copyOfBitVector);
    }

    @Test
    void fromBitString() {
        BitVector bitVector = BitVector.fromBitString("101101");
        assertEquals(BitVectorTest.bitVector, bitVector);
    }

    @Test
    void and() {
        BitVector bitVector = firstBitVector.and(secondBitVector);
        assertArrayEquals(new boolean[]{true, false, false, true, false, false}, bitVector.getElements());
    }

    @Test
    void or() {
        BitVector bitVector = firstBitVector.or(secondBitVector);
        assertArrayEquals(new boolean[]{true, true, true, true, false, true}, bitVector.getElements());
    }

    @Test
    void xor() {
        BitVector bitVector = firstBitVector.xor(secondBitVector);
        assertArrayEquals(new boolean[]{false, true, true, false, false, true}, bitVector.getElements());
    }

    @Test
    void toBitString() {
        String bitString = "101101";
        BitVector bitVector = BitVector.fromBitString(bitString);
        assertEquals(bitString, bitVector.toBitString());
    }
}