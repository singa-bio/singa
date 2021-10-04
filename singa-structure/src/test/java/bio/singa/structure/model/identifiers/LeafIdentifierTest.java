package bio.singa.structure.model.identifiers;

import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LeafIdentifierTest {

    private static LeafSubstructure leaf;
    private static Structure structure;

    @BeforeAll
    static void initialize() {
        structure = StructureParser.pdb()
                .pdbIdentifier("2w0l")
                .parse();
        leaf = structure.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().getSerial() == 27 && leafSubstructure.getIdentifier().getInsertionCode() == 'A')
                .findAny().orElseThrow(() -> new IllegalStateException("unable to find requested leaf"));
    }

    @Test
    void fromString() {
        PdbLeafIdentifier leafIdentifier = PdbLeafIdentifier.fromString("2w0l-1-A-27A");
        assertEquals(leaf.getIdentifier(), leafIdentifier);
    }

    @Test
    void fromStringCharacter() {
        PdbLeafIdentifier leafIdentifier = PdbLeafIdentifier.fromString("6bb4-1-I-82c");
        assertNotNull(leafIdentifier);
    }

    @Test
    void fromSimpleString() {
        PdbLeafIdentifier leafIdentifier = PdbLeafIdentifier.fromSimpleString("A-27A");
        assertEquals(leaf.getIdentifier().getChainIdentifier(), leafIdentifier.getChainIdentifier());
        assertEquals(leaf.getIdentifier().getSerial(), leafIdentifier.getSerial());
        assertEquals(leaf.getIdentifier().getInsertionCode(), leafIdentifier.getInsertionCode());
    }
}
