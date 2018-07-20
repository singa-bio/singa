package bio.singa.structure.model.oak;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.families.MatcherFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
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
    public void setUp() {
        Structure motifStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("Asn_3m4p.pdb"))
                .everything()
                .parse();
        structuralMotif = StructuralMotif.fromLeafSubstructures(motifStructure.getAllLeafSubstructures());
    }

    @Test
    public void shouldAssignExchanges() {
        StructuralMotifs.assignComplexExchanges(structuralMotif, MatcherFamily.GUTTERIDGE);
        assertTrue(MatcherFamily.GUTTERIDGE.stream()
                .map(MatcherFamily::getMembers)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()).containsAll(structuralMotif.getAllAminoAcids()
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
                .map(Structure::getAllLeafSubstructures)
                .map(StructuralMotif::fromLeafSubstructures)
                .collect(Collectors.toList());
        assertEquals(StructuralMotifs.calculateRmsdMatrix(input, false).getRowDimension(), input.size());
    }
}