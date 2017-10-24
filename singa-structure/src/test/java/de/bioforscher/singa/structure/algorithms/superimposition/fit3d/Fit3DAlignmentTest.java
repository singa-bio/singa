package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.chemistry.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.chemistry.parser.plip.InteractionContainer;
import de.bioforscher.singa.chemistry.parser.plip.PlipParser;
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
import de.bioforscher.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.families.MatcherFamily;
import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifiers;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.model.oak.OakStructure;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import de.bioforscher.singa.structure.parser.plip.InteractionContainer;
import de.bioforscher.singa.structure.parser.plip.PlipParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.bioforscher.singa.structure.model.oak.StructuralEntityFilter.AtomFilter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
                .fileLocation(Resources.getResourceAsFileLocation("1GL0_HDS_intra_E-H57_E-D102_E-S195.pdb"))
                .parse();
        this.queryMotif = StructuralMotif.fromLeafIdentifiers(motifContainingStructure,
                LeafIdentifiers.of("E-57", "E-102", "E-195"));
        this.queryMotif.addExchangeableFamily(LeafIdentifier.fromString("E-57"), AminoAcidFamily.GLUTAMIC_ACID);
    }

    @Test
    public void shouldRunFit3DAlignment() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(this.queryMotif)
                .target(this.target.getAllChains().get(0))
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0005, matches.get(0).getRmsd(), 1E-4);
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
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0005, matches.get(0).getRmsd(), 1E-4);
    }

    @Test
    public void shouldRunFit3DAlignmentBatch() throws IOException {
        Structure nucleotideTarget = StructureParser.online()
                .pdbIdentifier("2EES")
                .chainIdentifier("A")
                .parse();
        StructuralMotif nucleotideMotif = StructuralMotif.fromLeafIdentifiers(nucleotideTarget,
                LeafIdentifiers.of("A-22", "A-51", "A-52", "A-74"));
        nucleotideMotif.addExchangeableFamily(LeafIdentifier.fromString("A-74"), NucleotideFamily.URIDINE);
        List<Path> targetStructures = Files.list(
                Paths.get(Resources.getResourceAsFileLocation("RF00167")))
                .collect(Collectors.toList());
        StructureParser.MultiParser multiParser = StructureParser.local()
                .paths(targetStructures)
                .everything();
        Fit3D fit3dBatch = Fit3DBuilder.create()
                .query(nucleotideMotif)
                .targets(multiParser)
                .maximalParallelism()
                .run();
        assertEquals(14, fit3dBatch.getMatches().size());
    }

    @Test
    public void shouldFindInterMolecularMatches() throws IOException {
        Structure target = StructureParser.online()
                .pdbIdentifier("4CHA")
                .everything()
                .parse();
        StructuralMotif queryMotif = StructuralMotif.fromLeafIdentifiers(target,
                LeafIdentifiers.of("B-57", "B-102", "C-195"));
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstModel())
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0000, matches.get(0).getRmsd(), 1E-6);
    }

    @Test
    public void shouldGenerateCombinations() {
        assertEquals(1L, StreamCombinations.combinations(3, this.queryMotif.getAllLeafSubstructures()).count());
    }

    @Test
    public void shouldAlignNucleotideMotif() throws IOException {
        Structure nucleotideTarget = StructureParser.online()
                .pdbIdentifier("2EES")
                .chainIdentifier("A")
                .parse();
        StructuralMotif nucleotideMotif = StructuralMotif.fromLeafIdentifiers(nucleotideTarget,
                LeafIdentifiers.of("A-22", "A-51", "A-52", "A-74"));
        nucleotideMotif.addExchangeableFamily(LeafIdentifier.fromString("A-74"), NucleotideFamily.URIDINE);
        Fit3D fit3d = Fit3DBuilder.create()
                .query(nucleotideMotif)
                .target(nucleotideTarget.getAllChains().get(0))
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0, matches.get(0).getRmsd(), 1E-6);
    }

    @Test
    public void shouldFindLigandContainingMotif() throws IOException {
        Structure queryStructure = StructureParser.online()
                .pdbIdentifier("1ACJ")
                .everything()
                .parse();

        StructuralMotif queryMotif = StructuralMotif.fromLeafIdentifiers(queryStructure, LeafIdentifiers.of("A-84", "A-330", "A-999"));

        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(queryStructure.getAllModels().get(0))
                .run();

        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0, matches.get(0).getRmsd(), 1E-6);
    }

    @Test
    public void shouldSkipAlphaCarbonStructureInBatch() {
        this.queryMotif.addExchangeableFamilyToAll(MatcherFamily.ALL);
        List<String> alphaCarbonStructures = new ArrayList<>();
        alphaCarbonStructures.add("1zlg");
        StructureParser.MultiParser multiParser = StructureParser.online()
                .pdbIdentifiers(alphaCarbonStructures)
                .everything();
        Fit3D fit3d = Fit3DBuilder.create().query(this.queryMotif)
                .targets(multiParser)
                .skipAlphaCarbonTargets()
                .maximalParallelism()
                .atomFilter(AtomFilter.isArbitrary())
                .rmsdCutoff(3.0)
                .run();

        assertTrue(fit3d.getMatches().isEmpty());
    }

    @Test
    public void shouldSkipBackboneStructureInBatch() {
        this.queryMotif.addExchangeableFamilyToAll(MatcherFamily.ALL);
        List<String> alphaCarbonStructures = new ArrayList<>();
        alphaCarbonStructures.add("2plp");
        StructureParser.MultiParser multiParser = StructureParser.online()
                .pdbIdentifiers(alphaCarbonStructures)
                .everything();
        Fit3D fit3d = Fit3DBuilder.create().query(this.queryMotif)
                .targets(multiParser)
                .skipBackboneTargets()
                .maximalParallelism()
                .atomFilter(AtomFilter.isArbitrary())
                .rmsdCutoff(3.0)
                .run();

        assertTrue(fit3d.getMatches().isEmpty());
    }

    @Test
    public void shouldFindInteractionMotif() throws IOException {
        InteractionContainer interactionContainer = PlipParser.parse("1k1i",
                Resources.getResourceAsStream("plip/1k1i.xml"));
        Structure structure = StructureParser.online()
                .pdbIdentifier("1k1i")
                .chainIdentifier("A")
                .parse();
        interactionContainer.validateWithStructure((OakStructure) structure);
        interactionContainer.mapToPseudoAtoms((OakStructure) structure);
        StructuralMotif interactionMotif = StructuralMotif.fromLeafSubstructures(StructureParser.local()
                .inputStream(Resources.getResourceAsStream("1k1i_interaction_motif.pdb"))
                .parse()
                .getAllLeafSubstructures());

        Fit3D fit3d = Fit3DBuilder.create()
                .query(interactionMotif)
                .target(structure.getFirstChain())
                .atomFilter(AtomFilter.isArbitrary())
                .run();

        assertEquals(0.00, fit3d.getMatches().get(0).getRmsd(), 1E-2);
    }

}