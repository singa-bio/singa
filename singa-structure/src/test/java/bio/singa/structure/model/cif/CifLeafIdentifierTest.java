package bio.singa.structure.model.cif;

import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CifLeafIdentifierTest {

    @Test
    void fromStringStandard() {
        String testString = "1c0a-1-1-A-123";
        CifLeafIdentifier leafIdentifier = CifLeafIdentifier.fromString(testString);
        assertEquals("1c0a", leafIdentifier.getStructureIdentifier());
        assertEquals(1, leafIdentifier.getModelIdentifier());
        assertEquals("A", leafIdentifier.getChainIdentifier());
        assertEquals(123, leafIdentifier.getSerial());
        assertEquals(PdbLeafIdentifier.DEFAULT_INSERTION_CODE, leafIdentifier.getInsertionCode());
    }

    @Test
    void fromStringWrongSize() {
        String testString = "1c0a-1-1-B-23-A";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CifLeafIdentifier.fromString(testString);
        });
    }

}