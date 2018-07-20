package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.identifiers.LeafIdentifier;
import bio.singa.structure.model.identifiers.LeafIdentifiers;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureParser;
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
    public final TemporaryFolder testFolder = new TemporaryFolder();

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
        queryMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("E-57"), AminoAcidFamily.GLUTAMIC_ACID);
    }

    @Test
    public void shouldGetCsvRepresentation() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals("1gl0_E-57_E-102_E-195,4.680710257022384E-4,NaN,n/a,n/a,n/a,n/a", fit3d.getMatches().get(0).toCsvLine());
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

    @Test
    public void shouldAssembleCandidateStructuralMotif() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals(2, fit3d.getMatches().stream()
                .map(Fit3DMatch::getCandidateMotif)
                .count());
    }

    @Test
    public void shouldDetermineAlignedSequence(){
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals(2, fit3d.getMatches().stream()
                .map(Fit3DMatch::getAlignedSequence)
                .count());
    }

    @Test
    public void shouldDetermineType(){
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals(2, fit3d.getMatches().stream()
                .map(Fit3DMatch::getMatchType)
                .peek(System.out::println)
                .count());
    }
}