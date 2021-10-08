package bio.singa.structure.algorithms.superimposition.affinity;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.pdb.PdbAminoAcid;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import bio.singa.structure.model.general.StructuralMotif;
import bio.singa.structure.io.general.StructureParser;
import bio.singa.structure.io.general.StructureParserOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.ALANINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author cl
 */
class AffinityAlignmentTest {

    private List<StructuralMotif> input;

    @BeforeEach
    void initialize() throws Exception {
        StructureParserOptions structureParserOptions = new StructureParserOptions();
        structureParserOptions.inferIdentifierFromFileName(true);
        input = Files.list(Paths.get(Resources.getResourceAsFileLocation("consensus_alignment")))
                .map(path -> StructureParser.local()
                        .fileLocation(path.toString())
                        .everything()
                        .setOptions(structureParserOptions)
                        .parse())
                .map(Structure::getAllLeafSubstructures)
                .map(StructuralMotif::fromLeafSubstructures)
                .collect(Collectors.toList());
    }

    @Test
    void shouldRunAffinityAlignment() {
        AffinityAlignment affinityAlignment = AffinityAlignment.create()
                .inputStructuralMotifs(input)
                .run();
        assertEquals(3, affinityAlignment.getClusters().size());
        assertEquals(input.size(), affinityAlignment.getClusters().values().stream()
                .mapToInt(Collection::size)
                .sum());
    }

    @Test
    void shouldFailWithInputOfDifferentSize() {
        input.get(0).addLeafSubstructure(new PdbAminoAcid(new PdbLeafIdentifier(PdbLeafIdentifier.DEFAULT_PDB_IDENTIFIER, PdbLeafIdentifier.DEFAULT_MODEL_IDENTIFIER, PdbLeafIdentifier.DEFAULT_CHAIN_IDENTIFIER, 0), ALANINE));
        assertThrows(IllegalArgumentException.class,
                () -> AffinityAlignment.create()
                        .inputStructuralMotifs(input)
                        .run());
    }

}