package bio.singa.structure.model.molecules;

import bio.singa.structure.model.oak.LeafIdentifier;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * @author fk
 */
class MoleculeGraphsTest {

    @Test
    void shouldConvertStructureGraphToMoleculeGraph() {

        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1C0A")
                .parse();

        Optional<LeafSubstructure<?>> arginine = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "A", 1));
        Optional<LeafSubstructure<?>> amp = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "A", 800));
        Optional<LeafSubstructure<?>> a = structure.getLeafSubstructure(new LeafIdentifier("1C0A", 1, "B", 1));
    }

}