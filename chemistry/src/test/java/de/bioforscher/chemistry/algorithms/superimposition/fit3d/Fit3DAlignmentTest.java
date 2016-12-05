package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.mathematics.combinatorics.StreamCombinations;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fkaiser on 02.12.16.
 */
public class Fit3DAlignmentTest {

    private List<LeafSubstructure<?, ?>> queryMotif;
    private Structure target;

    @Before
    public void setUp() throws IOException {
        this.target = PDBParserService.parseProteinById("4CHA");
        this.queryMotif = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("motif_HDS_02.pdb")).getSubstructures().stream()
                .map(BranchSubstructure::getAtomContainingSubstructures)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Test
    public void shouldRunFit3DAlignment() {
        new Fit3DAlignment(this.queryMotif, this.target.getAllChains().get(1));
    }

    @Test
    public void shouldGenerateCombinations() {
        StreamCombinations.combinations(3, this.queryMotif).forEach(System.out::println);
    }
}