package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.parser.pdb.structures.PDBParserService;
import de.bioforscher.chemistry.physical.atoms.AtomFilter;
import de.bioforscher.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.chemistry.physical.branches.StructuralMotifs;
import de.bioforscher.chemistry.physical.families.MatcherFamily;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author fk
 */
public class Fit3DSiteAlignmentTest {

    private StructuralMotif bindingSite1;
    private StructuralMotif bindingSite2;

    @Before
    public void setUp() throws IOException {
        Structure bindingSiteStructure1 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("binding_sites_class2/Phe_3cmq.pdb"));
        this.bindingSite1 = StructuralMotif.fromLeafs(1, bindingSiteStructure1.getAllLeafs());
        Structure bindingSiteStructure2 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("binding_sites_class2/Lys_1bbu.pdb"));
        this.bindingSite2 = StructuralMotif.fromLeafs(1, bindingSiteStructure2
                .getAllLeafs());


//        Structure bindingSiteStructure1 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
//                .getResourceAsStream("2OCF_A.pdb"));
//        this.bindingSite1 = StructuralMotif.fromLeafs(1, bindingSiteStructure1,
//                LeafIdentifiers.of("A-346", "A-350", "A-353", "A-387", "A-394", "A-404", "A-524"));
//        Structure bindingSiteStructure2 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
//                .getResourceAsStream("1FDW_A.pdb"));
//        this.bindingSite2 = StructuralMotif.fromLeafs(1, bindingSiteStructure2,
//                LeafIdentifiers.of("A-143", "A-149", "A-186", "A-218", "A-225", "A-259"));
    }

    @Test
    public void shouldCreateBindingSiteAlignment() {
        Fit3D fit3d = Fit3DBuilder.create()
                .site(this.bindingSite1)
                .vs(this.bindingSite2)
                .ignoreSpecifiedExchanges()
                .atomFilter(AtomFilter.isBackbone())
                .run();
    }

    @Test
    public void shouldCreateGutteridgeBindingSiteAlignment() {
        // exchanges have only be added for one of the sites because their are transitive
        StructuralMotifs.assignExchanges(this.bindingSite1, MatcherFamily.GUTTERIDGE);
        Fit3D fit3d = Fit3DBuilder.create()
                .site(this.bindingSite1)
                .vs(this.bindingSite2)
                .restrictToSpecifiedExchanges()
                .atomFilter(AtomFilter.isBackbone())
                .run();
    }
}