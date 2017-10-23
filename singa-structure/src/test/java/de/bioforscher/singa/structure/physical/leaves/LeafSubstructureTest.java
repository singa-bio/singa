package de.bioforscher.singa.structure.physical.leaves;


import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.BeforeClass;

/**
 * @author cl
 */
public class LeafSubstructureTest {

    private static Structure structure;

    @BeforeClass
    public static void setup() {
        structure = StructureParser.online()
                .pdbIdentifier("1C0A")
                .parse();
    }

}