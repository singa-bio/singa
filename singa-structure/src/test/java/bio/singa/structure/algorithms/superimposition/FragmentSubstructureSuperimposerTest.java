package bio.singa.structure.algorithms.superimposition;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class FragmentSubstructureSuperimposerTest {

    @Test
    void shouldCalculateFragmentSuperimposition() {

        Collection<? extends LeafSubstructure> referenceLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();
        Collection<? extends LeafSubstructure> candidateLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("atp.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        SubstructureSuperimposition superimposition = FragmentSubstructureSuperimposer
                .calculateSubstructureSuperimposition(new ArrayList<>(referenceLeafSubstructure), new ArrayList<>(candidateLeafSubstructure));
        assertEquals(0.0, superimposition.getRmsd(), 1E-10);
    }
}