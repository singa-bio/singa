package bio.singa.structure.model.pdb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdbLeafIdentifierTest {

    @Test
    void fromStringStandard() {
        String testString = "1c0a-1-A-123";
        PdbLeafIdentifier leafIdentifier = PdbLeafIdentifier.fromString(testString);
        assertEquals("1c0a", leafIdentifier.getStructureIdentifier());
        assertEquals(1, leafIdentifier.getModelIdentifier());
        assertEquals("A", leafIdentifier.getChainIdentifier());
        assertEquals(123, leafIdentifier.getSerial());
        assertEquals(PdbLeafIdentifier.DEFAULT_INSERTION_CODE, leafIdentifier.getInsertionCode());
    }

    @Test
    void fromStringNegativeSerial() {
        String testString = "1c0a-1-B--23";
        PdbLeafIdentifier leafIdentifier = PdbLeafIdentifier.fromString(testString);
        assertEquals("1c0a", leafIdentifier.getStructureIdentifier());
        assertEquals(1, leafIdentifier.getModelIdentifier());
        assertEquals("B", leafIdentifier.getChainIdentifier());
        assertEquals(-23, leafIdentifier.getSerial());
        assertEquals(PdbLeafIdentifier.DEFAULT_INSERTION_CODE, leafIdentifier.getInsertionCode());
    }

    @Test
    void fromStringInsertionCode() {
        String testString = "1c0a-2-B-23A";
        PdbLeafIdentifier leafIdentifier = PdbLeafIdentifier.fromString(testString);
        assertEquals("1c0a", leafIdentifier.getStructureIdentifier());
        assertEquals(2, leafIdentifier.getModelIdentifier());
        assertEquals("B", leafIdentifier.getChainIdentifier());
        assertEquals(23, leafIdentifier.getSerial());
        assertEquals('A', leafIdentifier.getInsertionCode());
    }

    @Test
    void fromSimpleString() {
        PdbLeafIdentifier leafIdentifier = PdbLeafIdentifier.fromSimpleString("A-27A");
        assertEquals("A", leafIdentifier.getChainIdentifier());
        assertEquals(27, leafIdentifier.getSerial());
        assertEquals('A', leafIdentifier.getInsertionCode());
    }

    @Test
    void fromStringWrongSize() {
        String testString = "1c0a-1-1-B-23";
        assertThrows(IllegalArgumentException.class, () -> {
            PdbLeafIdentifier.fromString(testString);
        });
    }

}