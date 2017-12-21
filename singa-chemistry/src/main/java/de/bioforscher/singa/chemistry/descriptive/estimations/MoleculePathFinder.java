package de.bioforscher.singa.chemistry.descriptive.estimations;

import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeAtom;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBond;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeBondType;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.singa.structure.elements.Element;

import java.util.*;
import java.util.stream.Collectors;

import static de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph.isElement;
import static de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph.isOneOfElements;

/**
 * @author cl
 */
public class MoleculePathFinder {

    private final MoleculeGraph graph;
    private final List<LinkedList<MoleculeAtom>> candidates;

    private MoleculePathFinder(MoleculeGraph molecule) {
        graph = molecule;
        candidates = new LinkedList<>();
    }

    public static List<LinkedList<MoleculeAtom>> findPathInMolecule(MoleculeGraph molecule, LinkedList<Element> path) {
        MoleculePathFinder pathFinder = new MoleculePathFinder(molecule);
        // initialize starting points for unary path
        pathFinder.initializeCandidates(pathFinder.findStartingPoints(path));
        pathFinder.findAllCandidates(path);
        pathFinder.cleanCandidates();
        return pathFinder.candidates;
    }

    public static List<LinkedList<MoleculeAtom>> findMultiPathInMolecule(MoleculeGraph molecule, LinkedList<Set<Element>> multiPath) {
        MoleculePathFinder pathFinder = new MoleculePathFinder(molecule);
        // initialize starting points for multi path
        pathFinder.initializeCandidates(pathFinder.findStartingPointsForMultiPath(multiPath));
        pathFinder.findAllCandidatesForMultiPath(multiPath);
        pathFinder.cleanCandidates();
        return pathFinder.candidates;
    }

    public static List<LinkedList<MoleculeBond>> findAromaticPath(MoleculeGraph molecule) {
        MoleculePathFinder pathFinder = new MoleculePathFinder(molecule);
        // find all aromatic bonds
        List<MoleculeBond> aromaticBonds = pathFinder.graph.getEdges().stream()
                .filter(bond -> bond.getType() == MoleculeBondType.AROMATIC_BOND)
                .collect(Collectors.toList());
        // find continuous paths
        List<LinkedList<MoleculeBond>> continuousAromaticPaths = new ArrayList<>();
        // while bonds remain try to add them into the path
        while (!aromaticBonds.isEmpty()) {
            Iterator<MoleculeBond> iterator = aromaticBonds.listIterator();
            LinkedList<MoleculeBond> candidate = new LinkedList<>();
            candidate.add(iterator.next());
            iterator.remove();
            continuousAromaticPaths.add(candidate);
            // remembers if anything could be added
            boolean added = true;
            // while anything could be added
            while (added) {
                iterator = aromaticBonds.listIterator();
                added = false;
                // for each of the bonds
                while (iterator.hasNext()) {
                    MoleculeBond nextBond = iterator.next();
                    MoleculeBond first = candidate.getFirst();
                    MoleculeBond last = candidate.getLast();
                    if (last != first) {
                        // see if first fits
                        if (first.containsNode(nextBond.getSource()) || first.containsNode(nextBond.getTarget())) {
                            candidate.addFirst(nextBond);
                            added = true;
                            iterator.remove();
                            continue;
                        }
                    }
                    // see if last fits
                    if (last.containsNode(nextBond.getSource()) || last.containsNode(nextBond.getTarget())) {
                        candidate.addLast(nextBond);
                        added = true;
                        iterator.remove();
                    }
                }
            }
        }
        return continuousAromaticPaths;
    }

    private void initializeCandidates(List<MoleculeAtom> startingPoints) {
        for (MoleculeAtom startingPoint : startingPoints) {
            LinkedList<MoleculeAtom> candidate = new LinkedList<>();
            candidate.add(startingPoint);
            candidates.add(candidate);
        }
    }

    private List<MoleculeAtom> findStartingPoints(LinkedList<Element> path) {
        return graph.getNodes().stream()
                .filter(isElement(path.getFirst()))
                .collect(Collectors.toList());
    }

    private List<MoleculeAtom> findStartingPointsForMultiPath(LinkedList<Set<Element>> multiPath) {
        return graph.getNodes().stream()
                .filter(isOneOfElements(multiPath.getFirst()))
                .collect(Collectors.toList());
    }

    private void findAllCandidates(LinkedList<Element> path) {
        // skip starting point element
        Iterator<Element> pathIterator = path.iterator();
        pathIterator.next();
        // iteration
        int iteration = 1;
        // for each element in the path
        while (pathIterator.hasNext()) {
            Element nextElement = pathIterator.next();
            ListIterator<LinkedList<MoleculeAtom>> candidateIterator = candidates.listIterator();
            // and each candidate
            while (candidateIterator.hasNext()) {
                LinkedList<MoleculeAtom> candidate = candidateIterator.next();
                if (candidate.size() == iteration) {
                    // find neighbours of the last atom
                    for (MoleculeAtom neighbour : candidate.getLast().getNeighbours()) {
                        // that are the required element and are not already considered
                        if (neighbour.getElement().equals(nextElement) && !candidate.contains(neighbour)) {
                            LinkedList<MoleculeAtom> newCandidate = new LinkedList<>(candidate);
                            newCandidate.add(neighbour);
                            candidateIterator.add(newCandidate);
                        }
                    }
                }
            }
            // clean previous iterations
            cleanIteration(++iteration);
        }
    }

    private void findAllCandidatesForMultiPath(LinkedList<Set<Element>> path) {
        // skip starting point element
        Iterator<Set<Element>> pathIterator = path.iterator();
        pathIterator.next();
        // iteration
        int iteration = 1;
        // for each element in the path
        while (pathIterator.hasNext()) {
            Set<Element> nextElements = pathIterator.next();
            ListIterator<LinkedList<MoleculeAtom>> candidateIterator = candidates.listIterator();
            // and each candidate
            while (candidateIterator.hasNext()) {
                LinkedList<MoleculeAtom> candidate = candidateIterator.next();
                if (candidate.size() == iteration) {
                    // find neighbours of the last atom
                    for (MoleculeAtom neighbour : candidate.getLast().getNeighbours()) {
                        // that are the required element and are not already considered
                        if (nextElements.contains(neighbour.getElement()) && !candidate.contains(neighbour)) {
                            LinkedList<MoleculeAtom> newCandidate = new LinkedList<>(candidate);
                            newCandidate.add(neighbour);
                            candidateIterator.add(newCandidate);
                        }
                    }
                }
            }
            // clean previous iterations
            cleanIteration(++iteration);
        }
    }

    private void cleanIteration(int iteration) {
        // kick all elements from the previous iteration
        candidates.removeIf(moleculeAtoms -> moleculeAtoms.size() != iteration);
    }

    private void cleanCandidates() {
        ListIterator<LinkedList<MoleculeAtom>> candidateIterator = candidates.listIterator();
        // list to remember duplicates (to avoid concurrent modification)
        Set<List<MoleculeAtom>> listsToRemove = new HashSet<>();
        // for each candidate
        while (candidateIterator.hasNext()) {
            LinkedList<MoleculeAtom> reference = candidateIterator.next();
            ListIterator<LinkedList<MoleculeAtom>> duplicateIterator = candidates.listIterator(candidateIterator.nextIndex());
            // compare pairwise
            while (duplicateIterator.hasNext()) {
                LinkedList<MoleculeAtom> test = duplicateIterator.next();
                // test is not already flagged for removal AND
                // the last atom of the reference is equal to the first atom of the test (is reversed) AND
                // the reference contains the exact same atoms
                if (!listsToRemove.contains(test) && reference.getLast().equals(test.getFirst()) && reference.containsAll(test)) {
                    listsToRemove.add(duplicateIterator.previous());
                    break;
                }
            }
        }
        // remove all flagged lists
        candidates.removeAll(listsToRemove);
    }

}
