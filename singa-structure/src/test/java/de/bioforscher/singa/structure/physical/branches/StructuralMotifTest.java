package de.bioforscher.singa.structure.physical.branches;

import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
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
        this.structuralMotif = StructuralMotif.fromLeafSubstructures(motifStructure.getAllLeafSubstructures());
    }

    @Test
    public void shouldRetainSubstructureOrdering() {
        LeafSubstructure<?> aminoAcid1 = this.structuralMotif.getAllLeafSubstructures().get(this.structuralMotif.getAllLeafSubstructures().size() - 1);
        LeafSubstructure<?> aminoAcid2 = this.structuralMotif.getAllLeafSubstructures().get(0);
        List<LeafSubstructure<?>> aminoAcids = new ArrayList<>();
        aminoAcids.add(aminoAcid1);
        aminoAcids.add(aminoAcid2);
        StructuralMotif motif = StructuralMotif.fromLeafSubstructures(aminoAcids);
        assertTrue(motif.getAllLeafSubstructures().get(motif.getAllLeafSubstructures().size() - 1).getIdentifier().getSerial()
                > motif.getAllLeafSubstructures().get(0).getIdentifier().getSerial());
        assertTrue(motif.getAllLeafSubstructures().get(motif.getAllLeafSubstructures().size() - 1).getIdentifier().getSerial()
                < motif.getAllLeafSubstructures().get(0).getIdentifier().getSerial());
        StructuralMotif copiedMotif = motif.getCopy();
        assertTrue(copiedMotif.getAllLeafSubstructures().get(copiedMotif.getAllLeafSubstructures().size() - 1).getIdentifier().getSerial()
                > copiedMotif.getAllLeafSubstructures().get(0).getIdentifier().getSerial());
        assertTrue(copiedMotif.getAllLeafSubstructures().get(copiedMotif.getAllLeafSubstructures().size() - 1).getIdentifier().getSerial()
                < copiedMotif.getAllLeafSubstructures().get(0).getIdentifier().getSerial());
    }



}