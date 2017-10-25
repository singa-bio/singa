package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifiers;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
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
        this.target = StructureParser.online()
                .pdbIdentifier("1GL0")
                .parse();
        Structure motifContainingStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .parse();
        this.queryMotif = StructuralMotif.fromLeafIdentifiers(motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        this.queryMotif.addExchangeableFamily(LeafIdentifier.fromString("E-57"), AminoAcidFamily.GLUTAMIC_ACID);
    }

    @Test
    public void shouldGetCsvRepresentation() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(this.queryMotif)
                .target(this.target.getFirstChain())
                .run();
        assertEquals("1gl0_E-57_E-102_E-195,4.6807102570267135E-4,NaN", fit3d.getMatches().get(0).toCsvLine());
    }

    @Test
    public void shouldWriteSummaryFile() throws IOException {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(this.queryMotif)
                .target(this.target.getFirstChain())
                .run();
        File summaryFile = this.testFolder.newFile("summary.csv");
        fit3d.writeSummaryFile(summaryFile.toPath());
        assertTrue(summaryFile.exists());
    }
}