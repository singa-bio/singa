package bio.singa.mathematics.vectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BitVectorTest {

    RegularBitVector bitVector;

    @Before
    public void initialize() {
        bitVector = new RegularBitVector(true, false, true, true, false, true);
    }

    @Test
    public void getCopy() {
        BitVector copyOfBitVector = bitVector.getCopy();
        copyOfBitVector.getElements()[0] = false;
        assertTrue(bitVector.getElements()[0] != copyOfBitVector.getElements()[0]);
    }

    @Test
    public void equals() {
        BitVector copyOfBitVector = bitVector.getCopy();
        assertEquals(bitVector, copyOfBitVector);
    }

    @Test
    public void fromBitString() {
        BitVector bitVector = BitVector.fromBitString("101101");
        assertEquals(this.bitVector, bitVector);
    }
}