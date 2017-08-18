package de.bioforscher.singa.chemistry.physical.branches;


import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.families.MatcherFamily;
import de.bioforscher.singa.chemistry.physical.leaves.AminoAcid;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        Structure motifStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("Asn_3m4p.pdb"))
                .everything()
                .parse();
        this.structuralMotif = StructuralMotif.fromLeaves(motifStructure.getAllLeaves());
    }

    @Test
    public void shouldAssignExchanges() {
        StructuralMotifs.assignComplexExchanges(this.structuralMotif, MatcherFamily.GUTTERIDGE);
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
        List<StructuralMotif> input = Files.list(Paths.get(Resources.getResourceAsFileLocation("consensus_alignment")))
                .map(path -> StructureParser.local()
                        .fileLocation(path.toString())
                        .parse())
                .map(Structure::getAllLeaves)
                .map(StructuralMotif::fromLeaves)
                .collect(Collectors.toList());
        assertEquals(StructuralMotifs.calculateRmsdMatrix(input, false).getRowDimension(), input.size());
    }
}