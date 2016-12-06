package de.bioforscher.chemistry.algorithms.superimposition.consensus;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class ConsensusAlignmentTest {

    private List<List<LeafSubstructure<?, ?>>> input;

    @Before
    public void setUp() throws Exception {
        this.input = Files.list(Paths.get("/home/fkaiser/Workspace/IdeaProjects/singa/chemistry/src/test/resources/consensus_alignment"))
                .map(path -> {
                    try {
                        return PDBParserService.parsePDBFile(path.toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(Structure::getSubstructures)
                .flatMap(Collection::stream)
                .map(BranchSubstructure::getAtomContainingSubstructures)
                .collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInputOfDifferentSize() {
        this.input.get(0).add(new Residue(0, ResidueFamily.ALANINE));
        new ConsensusAlignment(this.input);
    }

    @Test
    public void shouldCreateConsensusAlignment() {
        ConsensusAlignment consensusAlignment = new ConsensusAlignment(this.input);
        System.out.println(consensusAlignment.getTopConsensusTree().toNewickString());
    }
}