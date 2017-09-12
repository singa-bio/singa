package de.bioforscher.singa.chemistry.physical.branches;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
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
    public void setup() throws Exception {
        Structure motifStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("Asn_3m4p.pdb"))
                .everything()
                .parse();
        this.structuralMotif = StructuralMotif.fromLeafIdentifiers(motifStructure.getAllLeafSubstructures());
    }

    @Test
    public void shouldRetainSubstructureOrdering() {
        LeafSubstructure<?, ?> aminoAcid1 = this.structuralMotif.getLeafSubstructures().get(this.structuralMotif.getLeafSubstructures().size() - 1);
        LeafSubstructure<?, ?> aminoAcid2 = this.structuralMotif.getLeafSubstructures().get(0);
        List<LeafSubstructure<?, ?>> aminoAcids = new ArrayList<>();
        aminoAcids.add(aminoAcid1);
        aminoAcids.add(aminoAcid2);
        StructuralMotif motif = StructuralMotif.fromLeafIdentifiers(aminoAcids);
        assertTrue(motif.getLeafSubstructures().get(motif.getLeafSubstructures().size() - 1).getIdentifier().getSerial()
                > motif.getLeafSubstructures().get(0).getIdentifier().getSerial());
        assertTrue(motif.getOrderedLeafSubstructures().get(motif.getLeafSubstructures().size() - 1).getIdentifier().getSerial()
                < motif.getOrderedLeafSubstructures().get(0).getIdentifier().getSerial());
        StructuralMotif copiedMotif = motif.getCopy();
        assertTrue(copiedMotif.getLeafSubstructures().get(copiedMotif.getLeafSubstructures().size() - 1).getIdentifier().getSerial()
                > copiedMotif.getLeafSubstructures().get(0).getIdentifier().getSerial());
        assertTrue(copiedMotif.getOrderedLeafSubstructures().get(copiedMotif.getLeafSubstructures().size() - 1).getIdentifier().getSerial()
                < copiedMotif.getOrderedLeafSubstructures().get(0).getIdentifier().getSerial());
    }



}