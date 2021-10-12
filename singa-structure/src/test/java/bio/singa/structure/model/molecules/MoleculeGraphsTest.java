package bio.singa.structure.model.molecules;

import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * @author fk
 */
class MoleculeGraphsTest {

    @Test
    void shouldConvertStructureGraphToMoleculeGraph() {

        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1c0a")
                .parse();

        Optional<? extends LeafSubstructure> arginine = structure.getLeafSubstructure(new PdbLeafIdentifier("1c0a", 1, "A", 1));
        Optional<? extends LeafSubstructure> amp = structure.getLeafSubstructure(new PdbLeafIdentifier("1c0a", 1, "A", 800));
        Optional<? extends LeafSubstructure> a = structure.getLeafSubstructure(new PdbLeafIdentifier("1c0a", 1, "B", 1));
    }

}