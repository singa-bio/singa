package de.bioforscher.singa.chemistry.physical.model;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class StructuresTest {
    @Test
    public void testIsAlphaCarbonStructure() throws Exception {
        Structure alphaCarbonStructure = StructureParser.online()
                .pdbIdentifier("1zlg")
                .parse();
        assertTrue(Structures.isAlphaCarbonStructure(alphaCarbonStructure));
    }

    @Test
    public void testIsBackboneOnlyStructure() {
        Structure alphaCarbonStructure = StructureParser.online()
                .pdbIdentifier("2plp")
                .parse();
        assertTrue(Structures.isBackboneStructure(alphaCarbonStructure));
    }
}