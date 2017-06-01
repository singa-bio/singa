package de.bioforscher.singa.chemistry.physical.branches;


import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.families.MatcherFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leaves.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class StructuralMotifsTest {
    private StructuralMotif structuralMotif;

    @Before
    public void setUp() throws Exception {
        Structure bindingSiteStructure1 = StructureParser.local()
                .fileLocation(Thread.currentThread().getContextClassLoader().getResource("Asn_3m4p.pdb").getFile())
                .everything()
                .parse();
        this.structuralMotif = StructuralMotif.fromLeaves(1, bindingSiteStructure1.getAllLeaves());
    }

    @Test
    public void shouldAssignExchanges() {
        StructuralMotifs.assignExchanges(this.structuralMotif, MatcherFamily.GUTTERIDGE);
        assertTrue(MatcherFamily.GUTTERIDGE.stream()
                .map(MatcherFamily::getMembers)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()).containsAll(this.structuralMotif.getAminoAcids()
                        .stream()
                        .map(AminoAcid::getExchangeableFamilies)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())));
    }

    @Test
    public void shouldCalculateRmsdMatrix() throws IOException {
        List<StructuralMotif> input = Files.list(Paths.get("src/test/resources/consensus_alignment"))
                .map(path -> StructureParser.local()
                        .fileLocation(path.toString())
                        .parse())
                .map(Structure::getAllLeaves)
                .map(leaves -> StructuralMotif.fromLeaves(0, leaves))
                .collect(Collectors.toList());
        assertEquals(StructuralMotifs.calculateRmsdMatrix(input, false).getRowDimension(), input.size());
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
    }
}