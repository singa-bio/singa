package de.bioforscher.chemistry.physical.branches;

import de.bioforscher.chemistry.parser.pdb.structures.PDBParserService;
import de.bioforscher.chemistry.physical.families.MatcherFamily;
import de.bioforscher.chemistry.physical.model.Structure;
import org.junit.Before;
import org.junit.Test;

/**
 * @author fk
 */
public class StructuralMotifsTest {
    private StructuralMotif bindingSite1;

    @Before
    public void setUp() throws Exception {
        Structure bindingSiteStructure1 = PDBParserService.parsePDBFile(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("Asn_3m4p.pdb"));
        this.bindingSite1 = StructuralMotif.fromLeafs(1, bindingSiteStructure1.getAllLeafs());
    }

    @Test
    public void shouldAssignExchanges(){

        StructuralMotifs.assignExchanges(this.bindingSite1, MatcherFamily.GUTTERIDGE);
        System.out.println();
    }
}