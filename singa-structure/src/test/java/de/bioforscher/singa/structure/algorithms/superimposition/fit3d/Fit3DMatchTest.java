package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifiers;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class Fit3DMatchTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private Structure target;
    private StructuralMotif queryMotif;

    @Before
    public void setUp() {
        target = StructureParser.pdb()
                .pdbIdentifier("1GL0")
                .parse();
        Structure motifContainingStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .parse();
        queryMotif = StructuralMotif.fromLeafIdentifiers(motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        queryMotif.addExchangeableFamily(LeafIdentifier.fromString("E-57"), AminoAcidFamily.GLUTAMIC_ACID);
    }

    @Test
    public void shouldGetCsvRepresentation() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals("1gl0_E-57_E-102_E-195,4.6807102570267135E-4,NaN", fit3d.getMatches().get(0).toCsvLine());
    }

    @Test
    public void shouldWriteSummaryFile() throws IOException {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        File summaryFile = testFolder.newFile("summary.csv");
        fit3d.writeSummaryFile(summaryFile.toPath());
        assertTrue(summaryFile.exists());
    }
}