package bio.singa.structure.model.oak;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.structure.model.families.StructuralFamilies.Matchers.ALL_GUTTERIDGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fk
 */
class StructuralMotifsTest {

    private static StructuralMotif structuralMotif;

    @BeforeAll
    static void initialize() {
        Structure motifStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("Asn_3m4p.pdb"))
                .everything()
                .parse();
        structuralMotif = StructuralMotif.fromLeafSubstructures(motifStructure.getAllLeafSubstructures());
    }

    @Test
    void shouldAssignExchanges() {
        StructuralMotifs.assignComplexExchanges(structuralMotif, ALL_GUTTERIDGE);
        assertTrue(ALL_GUTTERIDGE.containsAll(structuralMotif.getAllAminoAcids()
                        .stream()
                        .map(structuralMotif::getExchangeableFamilies)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())));
    }

    @Test
    void shouldCalculateRmsdMatrix() throws IOException {
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