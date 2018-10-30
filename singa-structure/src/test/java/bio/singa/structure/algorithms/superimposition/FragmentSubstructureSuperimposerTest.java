package bio.singa.structure.algorithms.superimposition;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class FragmentSubstructureSuperimposerTest {

    @Test
    void shouldCalculateFragmentSuperimposition() {

        List<LeafSubstructure<?>> referenceLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();
        List<LeafSubstructure<?>> candidateLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("atp.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        SubstructureSuperimposition superimposition = FragmentSubstructureSuperimposer
                .calculateSubstructureSuperimposition(referenceLeafSubstructure, candidateLeafSubstructure);
        assertEquals(0.0, superimposition.getRmsd(), 1E-10);
    }
}