package de.bioforscher.chemistry.algorithms.superimposition.consensus;

import de.bioforscher.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
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

import static de.bioforscher.chemistry.parser.pdb.structures.StructureSources.PDB_FILE;

/**
 * @author fk
 */
public class ConsensusAlignmentTest {

    private List<List<LeafSubstructure<?, ?>>> input;

    @Before
    public void setUp() throws Exception {
        this.input = Files.list(Paths.get("src/test/resources/consensus_alignment"))
                .map(path -> StructureParser.local()
                        .fileLocation(path.toString())
                        .parse())
                .map(Structure::getAllLeafs)
                .collect(Collectors.toList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithInputOfDifferentSize() {
        this.input.get(0).add(new AminoAcid(0, AminoAcidFamily.ALANINE));
        new ConsensusAlignment(this.input);
    }

    @Test
    public void shouldCreateConsensusAlignment() {
        ConsensusAlignment consensusAlignment = new ConsensusAlignment(this.input, 0.6);
        System.out.println(consensusAlignment.getTopConsensusTree().toNewickString());
        List<LeafSubstructure<?, ?>> consensusMotif = consensusAlignment.getTopConsensusTree().getRoot().getData().getLeafSubstructures();
        String consensusPDB = consensusMotif.stream().map(LeafSubstructure::getPdbLines)
                .flatMap(Collection::stream)
                .collect(Collectors.joining("\n"));
        System.out.println(consensusPDB);
    }
}