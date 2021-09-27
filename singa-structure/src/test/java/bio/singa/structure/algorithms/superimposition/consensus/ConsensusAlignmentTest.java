package bio.singa.structure.algorithms.superimposition.consensus;


import bio.singa.core.utility.Resources;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.oak.LeafIdentifier;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.OakAminoAcid;
import bio.singa.structure.model.oak.StructuralEntityFilter;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author fk
 */
class ConsensusAlignmentTest {

    private static Path directory;
    private List<StructuralMotif> input;

    @BeforeAll
    static void initializeDirectory() throws IOException {
        directory = Files.createTempDirectory("junit-tests");
    }

    @BeforeEach
    void initialize() throws IOException {
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
    void shouldFailWithInputOfDifferentSize() {
        input.get(0).addLeafSubstructure(new OakAminoAcid(new LeafIdentifier(0), AminoAcidFamily.ALANINE));
        assertThrows(ConsensusException.class,
                () -> ConsensusBuilder.create()
                        .inputStructuralMotifs(input)
                        .run());
    }

    @Test
    void shouldCreateConsensusAlignment() throws IOException {
        ConsensusAlignment consensusAlignment = ConsensusBuilder.create()
                .inputStructuralMotifs(input)
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .clusterCutoff(0.6)
                .run();
        consensusAlignment.writeClusters(directory);
        assertEquals(input.size(), consensusAlignment.getTopConsensusTree().getLeafNodes().size());
    }

    @Test
    void shouldCreateConsensusAlignmentWithRepresentationScheme() throws IOException {
        ConsensusAlignment consensusAlignment = ConsensusBuilder.create()
                .inputStructuralMotifs(input)
                .representationSchemeType(RepresentationSchemeType.BETA_CARBON)
                .clusterCutoff(0.2)
                .alignWithinClusters(true)
                .idealSuperimposition(true)
                .run();
        consensusAlignment.writeClusters(directory);
        assertEquals(input.size(), consensusAlignment.getTopConsensusTree().getLeafNodes().size());
    }
}