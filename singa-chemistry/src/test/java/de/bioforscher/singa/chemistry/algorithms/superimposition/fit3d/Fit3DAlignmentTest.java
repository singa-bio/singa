package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.singa.chemistry.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.physical.branches.StructuralMotif;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.families.MatcherFamily;
import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifiers;
import de.bioforscher.singa.chemistry.physical.model.StructuralEntityFilter.AtomFilter;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.mathematics.combinatorics.StreamCombinations;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private StructuralMotif queryMotif;
    private Structure target;

    @Before
    public void setUp() throws IOException {
        this.target = StructureParser.online()
                .pdbIdentifier("1GL0")
                .parse();
        Structure motifContainingStructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFilepath("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .parse();
        this.queryMotif = StructuralMotif.fromLeaves(motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        this.queryMotif.addExchangeableFamily(LeafIdentifier.fromString("E-57"), AminoAcidFamily.GLUTAMIC_ACID);
    }

    @Test
    public void shouldRunFit3DAlignment() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(this.queryMotif)
                .target(this.target.getAllChains().get(0))
                .run();
        TreeMap<Double, SubstructureSuperimposition> matches = fit3d.getMatches();
        assertEquals(0.0005, matches.firstKey(), 1E-4);
    }

    @Test
    public void shouldRunFit3DAlignmentWithExchangesAgainstAll() {
        this.queryMotif.addExchangeableFamily(LeafIdentifier.fromString("E-57"), MatcherFamily.ALL);
        Fit3D fit3d = Fit3DBuilder.create()
                .query(this.queryMotif)
                .target(this.target.getAllChains().get(0))
                .atomFilter(AtomFilter.isArbitrary())
                .rmsdCutoff(1.0)
                .run();
        TreeMap<Double, SubstructureSuperimposition> matches = fit3d.getMatches();
        assertEquals(0.0005, matches.firstKey(), 1E-4);
    }

    @Test
    public void shouldRunFit3DAlignmentBatch() throws IOException {
        Structure nucleotideTarget = StructureParser.online()
                .pdbIdentifier("2EES")
                .chainIdentifier("A")
                .parse();
        StructuralMotif nucleotideMotif = StructuralMotif.fromLeaves(nucleotideTarget,
                LeafIdentifiers.of("A-22", "A-51", "A-52", "A-74"));
        nucleotideMotif.addExchangeableFamily(LeafIdentifier.fromString("A-74"), NucleotideFamily.URIDINE);
        List<Path> targetStructures = Files.list(
                Paths.get(Resources.getResourceAsFilepath("RF00167")))
                .collect(Collectors.toList());
        StructureParser.MultiParser multiParser = StructureParser.local()
                .paths(targetStructures)
                .everything();
        Fit3D fit3dBatch = Fit3DBuilder.create()
                .query(nucleotideMotif)
                .targets(multiParser)
                .maximalParallelism()
                .run();
        assertEquals(fit3dBatch.getMatches().size(), 14);
    }

    @Test
    public void shouldFindInterMolecularMatches() throws IOException {
        Structure target = StructureParser.online()
                .pdbIdentifier("4CHA")
                .everything()
                .parse();
        StructuralMotif queryMotif = StructuralMotif.fromLeaves(target,
                LeafIdentifiers.of("B-57", "B-102", "C-195"));
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getAllModels().get(0))
                .run();
        TreeMap<Double, SubstructureSuperimposition> matches = fit3d.getMatches();
        assertEquals(0.0000, matches.firstKey(), 1E-6);
    }

    @Test
    public void shouldGenerateCombinations() {
        assertEquals(1L, StreamCombinations.combinations(3, this.queryMotif.getLeafSubstructures()).count());
    }

    @Test
    public void shouldAlignNucleotideMotif() throws IOException {
        Structure nucleotideTarget = StructureParser.online()
                .pdbIdentifier("2EES")
                .chainIdentifier("A")
                .parse();
        StructuralMotif nucleotideMotif = StructuralMotif.fromLeaves(nucleotideTarget,
                LeafIdentifiers.of("A-22", "A-51", "A-52", "A-74"));
        nucleotideMotif.addExchangeableFamily(LeafIdentifier.fromString("A-74"), NucleotideFamily.URIDINE);
        Fit3D fit3d = Fit3DBuilder.create()
                .query(nucleotideMotif)
                .target(nucleotideTarget.getAllChains().get(0))
                .run();
        TreeMap<Double, SubstructureSuperimposition> matches = fit3d.getMatches();
        assertEquals(0.0, matches.firstKey(), 1E-6);
    }

    @Test
    public void shouldFindLigandContainingMotif() throws IOException {
        Structure queryStructure = StructureParser.online()
                .pdbIdentifier("1ACJ")
                .everything()
                .parse();

        StructuralMotif queryMotif = StructuralMotif.fromLeaves(queryStructure, LeafIdentifiers.of("A-84", "A-330", "A-999"));

        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(queryStructure.getAllModels().get(0))
                .run();

        TreeMap<Double, SubstructureSuperimposition> matches = fit3d.getMatches();
        assertEquals(0.0, matches.firstKey(), 1E-6);
    }

//    @Test
//    public void shouldAlignKDEEH() throws IOException {
//
//        Structure target1 = PDBParserService.parseProteinById("1BKH", "A");
//
//        // KDEEH template motif
//        Structure motifContainingStructure = PDBParserService.parsePDBFile("motif_KDEEH.pdb");
//        StructuralMotif motif = StructuralMotif.fromLeaves(1, motifContainingStructure,
//                LeafIdentifiers.of("A-164", "A-195", "A-221", "A-247", "A-297"));
//        motif.addExchangeableFamily(LeafIdentifier.fromString("A-164"), AminoAcidFamily.HISTIDINE);
//        motif.addExchangeableFamily(LeafIdentifier.fromString("E-247"), AminoAcidFamily.ASPARTIC_ACID);
//        motif.addExchangeableFamily(LeafIdentifier.fromString("E-247"), AminoAcidFamily.ASPARAGINE);
//        motif.addExchangeableFamily(LeafIdentifier.fromString("H-297"), AminoAcidFamily.LYSINE);
//
//        Fit3D fit3d = Fit3DBuilder.create()
//                .query(motif)
//                .target(target1.getAllChains().get(0))
//                .run();
//    }
}