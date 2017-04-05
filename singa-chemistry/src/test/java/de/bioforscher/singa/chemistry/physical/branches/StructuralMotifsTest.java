package de.bioforscher.singa.chemistry.physical.branches;


import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.families.MatcherFamily;
import de.bioforscher.singa.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class StructuralMotifsTest {
    private StructuralMotif structuralMotif;

    @Before
    public void setUp() throws Exception {
        Structure bindingSiteStructure1 = StructureParser.local()
                .fileLocation(Thread.currentThread().getContextClassLoader().getResource("Asn_3m4p.pdb").getFile())
                .everything()
                .parse();
        this.structuralMotif = StructuralMotif.fromLeafs(1, bindingSiteStructure1.getAllLeafs());
    }

    @Test
    public void shouldAssignExchanges() {
        StructuralMotifs.assignExchanges(this.structuralMotif, MatcherFamily.GUTTERIDGE);
        Assert.assertTrue(MatcherFamily.GUTTERIDGE.stream()
                .map(MatcherFamily::getMembers)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()).containsAll(this.structuralMotif.getAminoAcids()
                        .stream()
                        .map(AminoAcid::getExchangeableFamilies)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())));
        System.out.println();
    }
}