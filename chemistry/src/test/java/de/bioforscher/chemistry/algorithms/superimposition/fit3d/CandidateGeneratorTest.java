package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.parser.pdb.PDBParserService;
import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fkaiser on 06.12.16.
 */
@Deprecated
public class CandidateGeneratorTest {
    private Structure target;
    private List<LeafSubstructure<?, ?>> queryMotif;

    @Before
    public void setUp() throws Exception {
        this.target = PDBParserService.parseProteinById("4CHA");
        this.queryMotif = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("motif_HDS_02.pdb")).getSubstructures().stream()
                .map(BranchSubstructure::getAtomContainingSubstructures)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        ((Residue) this.queryMotif.get(0)).addExchangeableType(ResidueFamily.GLUTAMIC_ACID);
    }

    @Test
    public void shouldGenerateValidCandidates() {
        Fit3DAlignment fit3d = new Fit3DAlignment(this.queryMotif, this.target.getAllChains().get(1));
        List<List<LeafSubstructure<?, ?>>> environments = fit3d.getEnvironments();
        CandidateGenerator candidateGenerator = new CandidateGenerator(this.queryMotif, environments.get(4));
    }
}