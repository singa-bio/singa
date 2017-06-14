package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class StructuralMotifTest {

    private StructuralMotif structuralMotif;

    @Before
    public void setUp() throws Exception {
        Structure motifStructure = StructureParser.local()
                .fileLocation(TestUtils.getResourceAsFilepath("Asn_3m4p.pdb"))
                .everything()
                .parse();
        this.structuralMotif = StructuralMotif.fromLeaves(1, motifStructure.getAllLeaves());
    }

    @Test
    public void shouldRetainSubstructureOrdering() {
        LeafSubstructure<?, ?> aminoAcid1 = this.structuralMotif.getLeafSubstructures().get(this.structuralMotif.getLeafSubstructures().size() - 1);
        LeafSubstructure<?, ?> aminoAcid2 = this.structuralMotif.getLeafSubstructures().get(0);
        List<LeafSubstructure<?, ?>> aminoAcids = new ArrayList<>();
        aminoAcids.add(aminoAcid1);
        aminoAcids.add(aminoAcid2);
        StructuralMotif motif = StructuralMotif.fromLeaves(aminoAcids);
        assertTrue(motif.getLeafSubstructures().get(motif.getLeafSubstructures().size() - 1).getLeafIdentifier().getIdentifier()
                > motif.getLeafSubstructures().get(0).getLeafIdentifier().getIdentifier());
        assertTrue(motif.getOrderedLeafSubstructures().get(motif.getLeafSubstructures().size() - 1).getLeafIdentifier().getIdentifier()
                < motif.getOrderedLeafSubstructures().get(0).getLeafIdentifier().getIdentifier());
        StructuralMotif copiedMotif = motif.getCopy();
        assertTrue(copiedMotif.getLeafSubstructures().get(copiedMotif.getLeafSubstructures().size() - 1).getLeafIdentifier().getIdentifier()
                > copiedMotif.getLeafSubstructures().get(0).getLeafIdentifier().getIdentifier());
        assertTrue(copiedMotif.getOrderedLeafSubstructures().get(copiedMotif.getLeafSubstructures().size() - 1).getLeafIdentifier().getIdentifier()
                < copiedMotif.getOrderedLeafSubstructures().get(0).getLeafIdentifier().getIdentifier());
    }
}