package bio.singa.structure.algorithms.molecules;

import bio.singa.chemistry.model.MoleculeAtom;
import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.core.utility.Pair;
import bio.singa.core.utility.Resources;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.molecules.MoleculeGraphs;
import bio.singa.structure.model.pdb.PdbLeafSubstructure;
import bio.singa.structure.io.general.StructureParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fk
 */
class MoleculeIsomorphismFinderTest {

    private static MoleculeGraph targetGraph;

    @BeforeAll
    static void initialize() {
        Collection<? extends LeafSubstructure> targetLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("atp.pdb"))
                .parse().getAllLeafSubstructures();
        targetGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) targetLeafSubstructure.iterator().next());
    }

    @Test
    void shouldFindAdeninePattern() {

        Set<Integer> correctIdentifiers = Stream.of(31, 30, 29, 26, 25, 21, 17, 13, 27, 28)
                .collect(Collectors.toSet());

        Collection<? extends LeafSubstructure> patternLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        MoleculeGraph patternGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) patternLeafSubstructure.iterator().next());

        MoleculeIsomorphism moleculeIsomorphism = MoleculeIsomorphismFinder.of(patternGraph, targetGraph);

        assertEquals(1, moleculeIsomorphism.getFullMatches().size());

        MoleculeGraph fullMatch = moleculeIsomorphism.getFullMatches().get(0);
        List<Integer> matchingIdentifiers = fullMatch.getNodes().stream()
                .map(MoleculeAtom::getIdentifier)
                .collect(Collectors.toList());
        assertEquals(correctIdentifiers.size(), matchingIdentifiers.size());
        assertTrue(matchingIdentifiers.containsAll(correctIdentifiers));

        List<Pair<MoleculeAtom>> atomPairs = moleculeIsomorphism.getAtomPairs(fullMatch);
        assertTrue(atomPairs.get(5).getFirst().getIdentifier() == 3 && atomPairs.get(5).getSecond().getIdentifier() == 21);
        assertTrue(atomPairs.get(8).getFirst().getIdentifier() == 10 && atomPairs.get(8).getSecond().getIdentifier() == 31);
    }

    @Test
    void shouldFindExtendedAdeninePattern() {

        Set<Integer> correctIdentifiers = Stream.of(31, 30, 29, 26, 25, 21, 17, 13, 27, 28, 12)
                .collect(Collectors.toSet());

        Collection<? extends LeafSubstructure> patternLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine_1.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        MoleculeGraph patternGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) patternLeafSubstructure.iterator().next());

        MoleculeIsomorphism moleculeIsomorphism = MoleculeIsomorphismFinder.of(patternGraph, targetGraph);

        assertEquals(1, moleculeIsomorphism.getFullMatches().size());

        MoleculeGraph fullMatch = moleculeIsomorphism.getFullMatches().get(0);
        List<Integer> matchingIdentifiers = fullMatch.getNodes().stream()
                .map(MoleculeAtom::getIdentifier)
                .collect(Collectors.toList());
        assertEquals(correctIdentifiers.size(), matchingIdentifiers.size());
        assertTrue(matchingIdentifiers.containsAll(correctIdentifiers));
    }

    @Test
    void shouldFindRingPattern() {

        Set<Integer> correctIdentifiers1 = Stream.of(21, 25, 26, 27, 13, 17, 31)
                .collect(Collectors.toSet());
        Set<Integer> correctIdentifiers2 = Stream.of(21, 25, 26, 27, 13, 17, 28)
                .collect(Collectors.toSet());

        Collection<? extends LeafSubstructure> patternLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine_2.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        MoleculeGraph patternGraph = MoleculeGraphs.createMoleculeGraphFromStructure((PdbLeafSubstructure) patternLeafSubstructure.iterator().next());

        MoleculeIsomorphism moleculeIsomorphism = MoleculeIsomorphismFinder.of(patternGraph, targetGraph, MoleculeIsomorphismFinder.AtomConditions.isSameElement(), (a, b) -> true);

        assertEquals(2, moleculeIsomorphism.getFullMatches().size());

        MoleculeGraph fullMatch1 = moleculeIsomorphism.getFullMatches().get(0);
        List<Integer> matchingIdentifiers1 = fullMatch1.getNodes().stream()
                .map(MoleculeAtom::getIdentifier)
                .collect(Collectors.toList());
        assertEquals(correctIdentifiers1.size(), matchingIdentifiers1.size());
        assertTrue(matchingIdentifiers1.containsAll(correctIdentifiers1));

        MoleculeGraph fullMatch2 = moleculeIsomorphism.getFullMatches().get(1);
        List<Integer> matchingIdentifiers2 = fullMatch2.getNodes().stream()
                .map(MoleculeAtom::getIdentifier)
                .collect(Collectors.toList());
        assertEquals(correctIdentifiers2.size(), matchingIdentifiers2.size());
        assertTrue(matchingIdentifiers2.containsAll(correctIdentifiers2));
    }
}