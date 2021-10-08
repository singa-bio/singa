package bio.singa.structure.algorithms.superimposition;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fk
 */
class MaximumCommonSubgraphSuperimposerTest {

    @Test
    void shouldCalculateMaximumCommonSubgraphSuperimposition() throws IOException {

        Collection<? extends LeafSubstructure> referenceLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine_shifted.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();
        Collection<? extends LeafSubstructure> candidateLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("atp.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        SubstructureSuperimposition superimposition = MaximumCommonSubgraphSuperimposer.calculateSubstructureSuperimposition(new ArrayList<>(referenceLeafSubstructure), new ArrayList<>(candidateLeafSubstructure));
        assertEquals(0.0, superimposition.getRmsd(), 1E-3);
    }
}