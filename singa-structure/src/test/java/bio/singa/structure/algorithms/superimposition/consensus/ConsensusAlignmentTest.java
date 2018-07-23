package bio.singa.structure.algorithms.superimposition.consensus;


import bio.singa.core.utility.Resources;
import bio.singa.structure.algorithms.superimposition.fit3d.representations.RepresentationSchemeType;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.OakAminoAcid;
import bio.singa.structure.model.oak.StructuralEntityFilter;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.StructureParserOptions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author fk
 */
public class ConsensusAlignmentTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    private List<StructuralMotif> input;

    @Before
    public void setUp() throws Exception {
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

    @Test(expected = ConsensusException.class)
    public void shouldFailWithInputOfDifferentSize() {
        input.get(0).addLeafSubstructure(new OakAminoAcid(new LeafIdentifier(0), AminoAcidFamily.ALANINE));
        ConsensusBuilder.create()
                .inputStructuralMotifs(input)
                .run();
    }

    @Test
    public void shouldCreateConsensusAlignment() throws IOException {
        ConsensusAlignment consensusAlignment = ConsensusBuilder.create()
                .inputStructuralMotifs(input)
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .clusterCutoff(0.6)
                .run();
        List<LeafSubstructure<?>> consensusMotif = consensusAlignment.getTopConsensusTree().getRoot().getData()
                .getStructuralMotif().getAllLeafSubstructures();
        consensusAlignment.writeClusters(folder.getRoot().toPath());
        assertEquals(input.size(), consensusAlignment.getTopConsensusTree().getLeafNodes().size());
    }

    @Test
    public void shouldCreateConsensusAlignmentWithRepresentationScheme() throws IOException {
        ConsensusAlignment consensusAlignment = ConsensusBuilder.create()
                .inputStructuralMotifs(input)
                .representationSchemeType(RepresentationSchemeType.BETA_CARBON)
                .clusterCutoff(0.2)
                .alignWithinClusters(true)
                .idealSuperimposition(true)
                .run();
        List<LeafSubstructure<?>> consensusMotif = consensusAlignment.getTopConsensusTree().getRoot().getData()
                .getStructuralMotif().getAllLeafSubstructures();
        consensusAlignment.writeClusters(folder.getRoot().toPath());
        assertEquals(input.size(), consensusAlignment.getTopConsensusTree().getLeafNodes().size());
    }
}