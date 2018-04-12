package de.bioforscher.singa.structure.algorithms.molecules;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.algorithms.graphs.isomorphism.RISubgraphFinder;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.structure.model.molecules.MoleculeAtom;
import de.bioforscher.singa.structure.model.molecules.MoleculeBond;
import de.bioforscher.singa.structure.model.molecules.MoleculeGraph;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class MoleculeIsomorphism {

    private final RISubgraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph> finder;
    private List<MoleculeGraph> fullMatches;

    public MoleculeIsomorphism(RISubgraphFinder<MoleculeAtom, MoleculeBond, Vector2D, Integer, MoleculeGraph> finder) {
        this.finder = finder;
        fullMatches = new ArrayList<>();
        determineMatches();
    }

    private void determineMatches() {
        for (List<Pair<MoleculeAtom>> pairs : finder.getFullMatchPairs()) {
            MoleculeGraph copy = (MoleculeGraph) finder.getTargetGraph().getCopy();
            List<Integer> identifiersToKeep = new ArrayList<>();
            for (Pair<MoleculeAtom> pair : pairs) {
                MoleculeAtom matchingAtom = pair.getSecond();
                identifiersToKeep.add(matchingAtom.getIdentifier());
            }

            List<Integer> identifiersToRemove = copy.getNodes().stream()
                    .map(MoleculeAtom::getIdentifier)
                    .collect(Collectors.toList());
            identifiersToRemove.removeAll(identifiersToKeep);
            identifiersToRemove.forEach(copy::removeNode);
            fullMatches.add(copy);
        }
    }

    public List<MoleculeGraph> getFullMatches() {
        return fullMatches;
    }

    public List<Pair<MoleculeAtom>> getAtomPairs(MoleculeGraph fullMatch) {
        if (!fullMatches.contains(fullMatch)) {
            throw new IllegalArgumentException("Pairs can only be retrieved for graphs that are full matches of the this isomorphism.");
        }
        return finder.getFullMatchPairs().get(fullMatches.indexOf(fullMatch));
    }

    /**
     * This method reduces the found matches to matches were different atoms were hit, i.e. matches that match the
     * identical set of atoms are considered to be equivalent.
     * <b>WARNING: Use only if pairing of atoms in pattern and target graph is irrelevant.</b>
     */
    public void reduceMatches() {
        Function<MoleculeGraph, Set<MoleculeAtom>> keyFunction = a -> new HashSet<>(a.getNodes());
        HashMap<Set<MoleculeAtom>, MoleculeGraph> uniqueMatches = fullMatches.stream()
                .collect(Collectors.toMap(keyFunction, p -> p, (p, q) -> p, HashMap::new));
        fullMatches = new ArrayList<>(uniqueMatches.values());
    }
}
