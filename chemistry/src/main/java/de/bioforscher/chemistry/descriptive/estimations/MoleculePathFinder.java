package de.bioforscher.chemistry.descriptive.estimations;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.molecules.MoleculeAtom;
import de.bioforscher.chemistry.descriptive.molecules.MoleculeGraph;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.bioforscher.chemistry.descriptive.molecules.MoleculeGraph.isElement;
import static de.bioforscher.chemistry.descriptive.molecules.MoleculeGraph.isOneOfElements;

/**
 * @author cl
 */
public class MoleculePathFinder {

    private MoleculeGraph graph;
    private List<LinkedList<MoleculeAtom>> candidates;

    private MoleculePathFinder(MoleculeGraph molecule) {
        this.graph = molecule;
        this.candidates = new LinkedList<>();
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

    private void initializeCandidates(List<MoleculeAtom> startingPoints) {
        for (MoleculeAtom startingPoint : startingPoints) {
            LinkedList<MoleculeAtom> candidate = new LinkedList<>();
            candidate.add(startingPoint);
            this.candidates.add(candidate);
        }
    }

    private List<MoleculeAtom> findStartingPoints(LinkedList<Element> path) {
        return this.graph.getNodes().stream()
                .filter(isElement(path.getFirst()))
                .collect(Collectors.toList());
    }

    private List<MoleculeAtom> findStartingPointsForMultiPath(LinkedList<Set<Element>> multiPath) {
        return this.graph.getNodes().stream()
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
            ListIterator<LinkedList<MoleculeAtom>> candidateIterator = this.candidates.listIterator();
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
            ListIterator<LinkedList<MoleculeAtom>> candidateIterator = this.candidates.listIterator();
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
        // renew iterator
        ListIterator<LinkedList<MoleculeAtom>> candidateIterator = this.candidates.listIterator();
        // kick all elements from the previous iteration
        while (candidateIterator.hasNext()) {
            if (candidateIterator.next().size() != iteration) {
                candidateIterator.remove();
            }
        }
    }

    private void cleanCandidates() {
        ListIterator<LinkedList<MoleculeAtom>> candidateIterator = this.candidates.listIterator();
        // list to remember duplicates (to avoid concurrent modification)
        Set<List<MoleculeAtom>> listsToRemove = new HashSet<>();
        // for each candidate
        while (candidateIterator.hasNext()) {
            LinkedList<MoleculeAtom> reference = candidateIterator.next();
            ListIterator<LinkedList<MoleculeAtom>> duplicateIterator = this.candidates.listIterator(candidateIterator.nextIndex());
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
        listsToRemove.forEach(this.candidates::remove);
    }

}
