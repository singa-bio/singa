package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Structure;
import de.bioforscher.mathematics.combinatorics.StreamCombinations;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * A test for the implementation of the Fit3D algorithm.
 *
 * @author fk
 */
public class Fit3DAlignmentTest {

    private List<LeafSubstructure<?, ?>> queryMotif;
    private Structure target;

    @Before
    public void setUp() throws IOException {
        this.target = PDBParserService.parseProteinById("1GL0");
        this.queryMotif = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb")).getSubstructures().stream()
                .map(BranchSubstructure::getAtomContainingSubstructures)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        ((Residue) this.queryMotif.get(0)).addExchangeableType(ResidueFamily.GLUTAMIC_ACID);
    }

    @Test
    public void shouldRunFit3DAlignment() {
        Fit3DAlignment fit3d = new Fit3DAlignment(this.queryMotif, this.target.getAllChains().get(0));
        TreeMap<Double, SubstructureSuperimposition> matches = fit3d.getMatches();
        assertEquals(0.0005, matches.firstKey(), 1E-4);
    }

    @Test
    public void shouldGenerateCombinations() {
        assertEquals(10L, StreamCombinations.combinations(3, this.queryMotif).count());
    }
}