package de.bioforscher.singa.structure.algorithms.superimposition;

import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author fk
 */
public class FragmentSubstructureSuperimposerTest {

    @Test
    public void shouldCalculateFragmentSuperimposition() {

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