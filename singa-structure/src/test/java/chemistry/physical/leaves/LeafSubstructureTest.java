package chemistry.physical.leaves;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.branches.Chain;
import de.bioforscher.singa.chemistry.physical.branches.StructuralModel;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @Test
    public void testFlatToString() {
        StructuralModel structuralModel = structure.getFirstModel();
        System.out.println(structuralModel);
        System.out.println(structuralModel.deepToString());
        System.out.println();

        Chain chain = structuralModel.getFirstChain();
        System.out.println(chain);
        System.out.println(chain.deepToString());
        System.out.println();

        LeafSubstructure<?, ?> firstLeaf = structure.getAllLeafSubstructures().iterator().next();
        System.out.println(firstLeaf);
        System.out.println(firstLeaf.flatToString());
        System.out.println(firstLeaf.deepToString());
        System.out.println();
    }


}