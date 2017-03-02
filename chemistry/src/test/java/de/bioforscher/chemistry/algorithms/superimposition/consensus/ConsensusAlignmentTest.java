package de.bioforscher.chemistry.algorithms.superimposition.consensus;

import de.bioforscher.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author fk
 */
public class ConsensusAlignmentTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private List<StructuralMotif> input;

    @Before
    public void setUp() throws Exception {
        this.input = Files.list(Paths.get("src/test/resources/motifs"))
                .map(path -> StructureParser.local()
                        .fileLocation(path.toString())
                        .parse())
                .map(Structure::getAllLeafs)
                .map(leaves -> StructuralMotif.fromLeafs(0, leaves))
                .collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInputOfDifferentSize() {
        this.input.get(0).addSubstructure(new AminoAcid(0, AminoAcidFamily.ALANINE));
        new ConsensusAlignment(this.input);
    }

    @Test
    public void shouldCreateConsensusAlignment() throws IOException {
        ConsensusAlignment consensusAlignment = new ConsensusAlignment(this.input, 0.6);
        List<LeafSubstructure<?, ?>> consensusMotif = consensusAlignment.getTopConsensusTree().getRoot().getData().getStructuralMotif().getLeafSubstructures();
        consensusAlignment.writeClusters(this.folder.getRoot().toPath());
        assertEquals(this.input.size(), consensusAlignment.getTopConsensusTree().getLeafNodes().size());
    }
}