package bio.singa.structure.algorithms.superimposition.fit3d;

import bio.singa.core.utility.Resources;
import bio.singa.mathematics.combinatorics.StreamCombinations;
import bio.singa.structure.algorithms.superimposition.SubstructureSuperimposition;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.oak.*;
import bio.singa.structure.model.interfaces.Structure;
import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.iterators.StructureIterator;
import bio.singa.structure.parser.plip.InteractionContainer;
import bio.singa.structure.parser.plip.PlipParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.structure.model.families.StructuralFamilies.AminoAcids.*;
import static bio.singa.structure.model.families.StructuralFamilies.Matchers.ALL;
import static bio.singa.structure.model.families.StructuralFamilies.Nucleotides.URIDINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A test for the implementation of the Fit3D algorithm.
 *
 * @author fk
 */
class Fit3DAlignmentTest {

    private StructuralMotif queryMotif;
    private  Structure target;

    @BeforeEach
    void initialize() {
        target = StructureParser.mmtf()
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
    void shouldRunFit3DAlignment() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getAllChains().get(0))
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0005, matches.get(0).getRmsd(), 1E-4);
    }

    @Test
    void shouldRunFit3DAlignmentWithExchangesAgainstAll() {
        queryMotif.addExchangeableFamilies(PdbLeafIdentifier.fromSimpleString("E-57"), ALL);
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getAllChains().get(0))
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .rmsdCutoff(1.0)
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0005, matches.get(0).getRmsd(), 1E-4);
    }

    @Test
    void shouldRunFit3DAlignmentBatch() throws IOException {
        Structure nucleotideTarget = StructureParser.pdb()
                .pdbIdentifier("2EES")
                .chainIdentifier("A")
                .parse();
        StructuralMotif nucleotideMotif = StructuralMotif.fromLeafIdentifiers(nucleotideTarget,
                LeafIdentifiers.of("A-22", "A-51", "A-52", "A-74"));
        nucleotideMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-74"), URIDINE);
        List<Path> targetStructures = Files.list(
                Paths.get(Resources.getResourceAsFileLocation("RF00167")))
                .collect(Collectors.toList());
        StructureIterator multiParser = StructureParser.local()
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
    void shouldRunFit3DAlignmentWithMMTF() {
        Structure target = StructureParser.mmtf()
                // Structure target = StructureParser.pdb()
                .pdbIdentifier("4CHA")
                .everything()
                .parse();
        StructuralMotif queryMotif = StructuralMotif.fromLeafIdentifiers(target,
                LeafIdentifiers.of("B-57", "B-102", "C-195"));
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getFirstModel())
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0000, matches.get(0).getRmsd(), 1E-6);
    }


    @Test
    void shouldRunFit3DAlignmentWithExchanges() {
        Structure target = StructureParser.pdb()
                .pdbIdentifier("2mnr")
                .everything()
                .parse();
        StructuralMotif structuralMotif = StructuralMotif.fromLeafSubstructures(StructureParser.local()
                .inputStream(Resources.getResourceAsStream("motif_KDEEH.pdb"))
                .parse()
                .getAllLeafSubstructures());
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-164"), HISTIDINE);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-247"), ASPARTIC_ACID);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-247"), ASPARAGINE);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-297"), LYSINE);

        Fit3D fit3d = Fit3DBuilder.create()
                .query(structuralMotif)
                .target(target.getFirstModel())
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0000, matches.get(0).getRmsd(), 1E-6);
    }

    @Test
    void shouldRunFit3DAlignmentWithExchangesAndFiltering() {
        Structure target = StructureParser.pdb()
                .pdbIdentifier("2mnr")
                .everything()
                .parse();
        StructuralMotif structuralMotif = StructuralMotif.fromLeafSubstructures(StructureParser.local()
                .inputStream(Resources.getResourceAsStream("motif_KDEEH.pdb"))
                .parse()
                .getAllLeafSubstructures());
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-164"), HISTIDINE);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-247"), ASPARTIC_ACID);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-247"), ASPARAGINE);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-297"), LYSINE);

        Fit3D fit3d = Fit3DBuilder.create()
                .query(structuralMotif)
                .target(target.getFirstModel())
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .filterEnvironments(3.0)
                .run();

        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0000, matches.get(0).getRmsd(), 1E-6);
    }


    @Test
    void shouldRunFit3DAlignmentAndExchangesWithMMTF() {
        Structure target = StructureParser.mmtf()
                .pdbIdentifier("2mnr")
                .everything()
                .parse();
        StructuralMotif structuralMotif = StructuralMotif.fromLeafSubstructures(StructureParser.local()
                .inputStream(Resources.getResourceAsStream("motif_KDEEH.pdb"))
                .parse()
                .getAllLeafSubstructures());
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-164"), HISTIDINE);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-247"), ASPARTIC_ACID);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-247"), ASPARAGINE);
        structuralMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-297"), LYSINE);

        Fit3D fit3d = Fit3DBuilder.create()
                .query(structuralMotif)
                .target(target.getFirstModel())
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0000, matches.get(0).getRmsd(), 1E-6);
    }


    @Test
    void shouldFindInterMolecularMatches() {
        Structure target = StructureParser.pdb()
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
    void shouldFindInterMolecularMatchesWithMMTF() {
        Structure target = StructureParser.mmtf()
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
    void shouldGenerateCombinations() {
        assertEquals(1L, StreamCombinations.combinations(3, queryMotif.getAllLeafSubstructures()).count());
    }

    @Test
    void shouldAlignNucleotideMotif() {
        Structure nucleotideTarget = StructureParser.pdb()
                .pdbIdentifier("2EES")
                .chainIdentifier("A")
                .parse();
        StructuralMotif nucleotideMotif = StructuralMotif.fromLeafIdentifiers(nucleotideTarget,
                LeafIdentifiers.of("A-22", "A-51", "A-52", "A-74"));
        nucleotideMotif.addExchangeableFamily(PdbLeafIdentifier.fromSimpleString("A-74"), URIDINE);
        Fit3D fit3d = Fit3DBuilder.create()
                .query(nucleotideMotif)
                .target(nucleotideTarget.getAllChains().get(0))
                .run();
        List<Fit3DMatch> matches = fit3d.getMatches();
        assertEquals(0.0, matches.get(0).getRmsd(), 1E-6);
    }

    @Test
    void shouldFindLigandContainingMotif() {
        Structure queryStructure = StructureParser.pdb()
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
    void shouldSkipAlphaCarbonStructureInBatch() {
        ALL.forEach(queryMotif::addExchangeableFamilyToAll);
        List<String> alphaCarbonStructures = new ArrayList<>();
        alphaCarbonStructures.add("1zlg");
        StructureIterator multiParser = StructureParser.pdb()
                .pdbIdentifiers(alphaCarbonStructures)
                .everything();
        Fit3D fit3d = Fit3DBuilder.create().query(queryMotif)
                .targets(multiParser)
                .skipAlphaCarbonTargets()
                .maximalParallelism()
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .rmsdCutoff(3.0)
                .run();

        assertTrue(fit3d.getMatches().isEmpty());
    }

    @Test
    void shouldSkipBackboneStructureInBatch() {
        ALL.forEach(queryMotif::addExchangeableFamilyToAll);
        List<String> alphaCarbonStructures = new ArrayList<>();
        alphaCarbonStructures.add("2plp");
        StructureIterator multiParser = StructureParser.pdb()
                .pdbIdentifiers(alphaCarbonStructures)
                .everything();
        Fit3D fit3d = Fit3DBuilder.create().query(queryMotif)
                .targets(multiParser)
                .skipBackboneTargets()
                .maximalParallelism()
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .rmsdCutoff(3.0)
                .run();

        assertTrue(fit3d.getMatches().isEmpty());
    }

    @Test
    void shouldFindInteractionMotif() {
        InteractionContainer interactionContainer = PlipParser.parse("1k1i",
                Resources.getResourceAsStream("plip/1k1i.xml"));
        Structure structure = StructureParser.pdb()
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
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .run();

        assertEquals(0.00, fit3d.getMatches().get(0).getRmsd(), 1E-2);
    }

    @Test
    void shouldHandleInsertionCodeMotifs() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("2w0l")
                .parse();

        List<PdbLeafIdentifier> leafIdentifiers = LeafIdentifiers.of("A-95A", "A-98", "A-100");
        StructuralMotif structuralMotif = StructuralMotif.fromLeafIdentifiers(structure, leafIdentifiers);

        Fit3D fit3d = Fit3DBuilder.create()
                .query(structuralMotif)
                .target(structure.getFirstChain())
                .run();
        assertEquals(0.00, fit3d.getMatches().get(0).getRmsd(), 1E-6);

        leafIdentifiers = LeafIdentifiers.of("A-95", "A-98", "A-100");
        structuralMotif = StructuralMotif.fromLeafIdentifiers(structure, leafIdentifiers);
        fit3d = Fit3DBuilder.create()
                .query(structuralMotif)
                .target(structure.getFirstChain())
                .run();
        assertEquals(0.00, fit3d.getMatches().get(0).getRmsd(), 1E-6);
    }

    @Test
    void shouldFindInsertionCodeMotifs() {
        Structure structure = StructureParser.pdb()
                .pdbIdentifier("1a0j")
                .parse();

        Structure targetStructure = StructureParser.pdb()
                .pdbIdentifier("1m9u")
                .parse();

        List<PdbLeafIdentifier> leafIdentifiers = LeafIdentifiers.of("A-57", "A-102", "A-193", "A-195");
        StructuralMotif structuralMotif = StructuralMotif.fromLeafIdentifiers(structure, leafIdentifiers);

        Fit3D fit3d = Fit3DBuilder.create()
                .query(structuralMotif)
                .target(targetStructure)
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .rmsdCutoff(3.0)
                .distanceTolerance(2.0)
                .run();

        assertTrue(fit3d.getMatches().stream()
                .map(Fit3DMatch::getSubstructureSuperimposition)
                .map(SubstructureSuperimposition::getStringRepresentation)
                .filter(string -> string.contains("A-98B"))
                .count() >= 1);
    }

    @Test
    void shouldAnnotateIdentifiers() {
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .target(target.getAllChains().get(0))
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .mapUniProtIdentifiers()
                .mapPfamIdentifiers()
                .mapECNumbers()
                .run();
        assertTrue(fit3d.getMatches().stream()
                .map(Fit3DMatch::getPfamIdentifiers)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Map::values)
                .flatMap(Collection::stream)
                .anyMatch(pfamIdentifier -> pfamIdentifier.getContent().equals("PF00089")));
        assertTrue(fit3d.getMatches().stream()
                .map(Fit3DMatch::getUniProtIdentifiers)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Map::values)
                .flatMap(Collection::stream)
                .anyMatch(uniProtIdentifier -> uniProtIdentifier.getContent().equals("P00766")));
        assertTrue(fit3d.getMatches().stream()
                .map(Fit3DMatch::getEcNumbers)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Map::values)
                .flatMap(Collection::stream)
                .anyMatch(ecNumber -> ecNumber.getContent().equals("3.4.21.1")));
    }

    @Test
    void shouldAnnotateIdentifiersInBatch() {
        StructureIterator multiParser = StructureParser.mmtf()
                .chainList(Paths.get(Resources.getResourceAsFileLocation("chain_list_PF00089.txt")), "\t")
                .everything();
        Fit3D fit3d = Fit3DBuilder.create()
                .query(queryMotif)
                .targets(multiParser)
                .maximalParallelism()
                .atomFilter(StructuralEntityFilter.AtomFilter.isArbitrary())
                .mapUniProtIdentifiers()
                .mapPfamIdentifiers()
                .mapECNumbers()
                .run();
        assertTrue(fit3d.getMatches().stream()
                .map(Fit3DMatch::getPfamIdentifiers)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Map::values)
                .flatMap(Collection::stream)
                .anyMatch(pfamIdentifier -> pfamIdentifier.getContent().equals("PF00089")));
        assertTrue(fit3d.getMatches().stream()
                .map(Fit3DMatch::getUniProtIdentifiers)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Map::values)
                .flatMap(Collection::stream)
                .anyMatch(uniProtIdentifier -> uniProtIdentifier.getContent().equals("Q9EXR9")));
        assertTrue(fit3d.getMatches().stream()
                .map(Fit3DMatch::getEcNumbers)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Map::values)
                .flatMap(Collection::stream)
                .anyMatch(ecNumber -> ecNumber.getContent().equals("3.4.21.5")));
    }
}