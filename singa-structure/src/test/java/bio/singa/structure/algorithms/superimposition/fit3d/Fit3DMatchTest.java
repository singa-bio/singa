package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.core.utility.Resources;
import bio.singa.structure.model.oak.LeafIdentifiers;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.model.oak.PdbLeafIdentifier;
import bio.singa.structure.model.oak.StructuralMotif;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.GLUTAMIC_ACID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fk
 */
class Fit3DMatchTest {

    private static Path directory;
    private static Structure target;
    private static StructuralMotif queryMotif;

    @BeforeAll
    static void initialize() throws IOException {
        directory = Files.createTempDirectory("junit-tests");
        target = StructureParser.pdb()
                .pdbIdentifier("1GL0")
                .parse();
        Structure motifContainingStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .parse();
        queryMotif = StructuralMotif.fromLeafIdentifiers(motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        queryMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("E-57"), GLUTAMIC_ACID);
    }

    @Test
    void shouldGetCsvRepresentation() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals("1gl0_E-57_E-102_E-195,4.680710257022384E-4,NaN,n/a,n/a,n/a,n/a", fit3d.getMatches().get(0).toCsvLine());
    }

    @Test
    void shouldWriteSummaryFile() throws IOException {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        Path summary = Files.createFile(directory.resolve("summary.csv"));
        fit3d.writeSummaryFile(summary);
        assertTrue(Files.size(summary) > 0);
    }

    @Test
    void shouldAssembleCandidateStructuralMotif() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals(2, fit3d.getMatches().stream()
                .map(Fit3DMatch::getCandidateMotif)
                .count());
    }

    @Test
    void shouldDetermineAlignedSequence(){
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals(2, fit3d.getMatches().stream()
                .map(Fit3DMatch::getAlignedSequence)
                .count());
    }

    @Test
    void shouldDetermineType(){
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstChain())
                .run();
        assertEquals(2, fit3d.getMatches().stream()
                .map(Fit3DMatch::getMatchType)
                .count());
    }
}