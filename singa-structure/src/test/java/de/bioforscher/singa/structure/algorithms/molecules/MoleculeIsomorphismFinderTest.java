package de.bioforscher.singa.structure.algorithms.molecules;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.molecules.MoleculeAtom;
import de.bioforscher.singa.structure.model.molecules.MoleculeGraph;
import de.bioforscher.singa.structure.model.molecules.MoleculeGraphs;
import de.bioforscher.singa.structure.model.oak.OakLeafSubstructure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author fk
 */
public class MoleculeIsomorphismFinderTest {

    private MoleculeGraph targetGraph;

    @Before
    public void setUp() {
        List<LeafSubstructure<?>> targetLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("atp.pdb"))
                .parse().getAllLeafSubstructures();
        targetGraph = MoleculeGraphs.createMoleculeGraphFromStructure((OakLeafSubstructure<?>) targetLeafSubstructure.get(0));
    }

    @Test
    public void shouldFindAdeninePattern() {

        Set<Integer> correctIdentifiers = Stream.of(31, 30, 29, 26, 25, 21, 17, 13, 27, 28)
                .collect(Collectors.toSet());

        List<LeafSubstructure<?>> patternLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        MoleculeGraph patternGraph = MoleculeGraphs.createMoleculeGraphFromStructure((OakLeafSubstructure<?>) patternLeafSubstructure.get(0));

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
    public void shouldFindExtendedAdeninePattern() {

        Set<Integer> correctIdentifiers = Stream.of(31, 30, 29, 26, 25, 21, 17, 13, 27, 28, 12)
                .collect(Collectors.toSet());

        List<LeafSubstructure<?>> patternLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine_1.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        MoleculeGraph patternGraph = MoleculeGraphs.createMoleculeGraphFromStructure((OakLeafSubstructure<?>) patternLeafSubstructure.get(0));

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
    public void shouldFindRingPattern() {

        Set<Integer> correctIdentifiers1 = Stream.of(21, 25, 26, 27, 13, 17, 31)
                .collect(Collectors.toSet());
        Set<Integer> correctIdentifiers2 = Stream.of(21, 25, 26, 27, 13, 17, 28)
                .collect(Collectors.toSet());

        List<LeafSubstructure<?>> patternLeafSubstructure = StructureParser.local()
                .fileLocation(Resources.getResourceAsFileLocation("adenine_2.pdb"))
                .everything()
                .parse().getAllLeafSubstructures();

        MoleculeGraph patternGraph = MoleculeGraphs.createMoleculeGraphFromStructure((OakLeafSubstructure<?>) patternLeafSubstructure.get(0));

        MoleculeIsomorphism moleculeIsomorphism = MoleculeIsomorphismFinder.of(patternGraph, targetGraph, MoleculeAtom::getElement, edge -> true);

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