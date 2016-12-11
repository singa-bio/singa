package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class constructs valid candidates for the Fit3D algorithm.
 * <p>
 * Consider Q=(H,D,S) to be the query motif of size <i>k</i>. Further, we allow exchanges that are:
 * <p>
 * <pre>
 *  E(H) = (E)
 * </pre>
 * <p>
 * For a given extracted environment N=(H,E,D1,S1,D2,H2,S2) only the following candidates of size <i>k</i>
 * are generated to be then aligned to the query motif:
 * <p>
 * <pre>
 *  C1 =   H | D1 | S1
 *  C2 =   H | D1 | S2
 *  C3 =   H | D2 | S1
 *  C4 =   H | D2 | S2
 *  C5 =   Q | D1 | S1
 *  C6 =   Q | D1 | S2
 *  C7 =   Q | D2 | S1
 *  C8 =   Q | D2 | S2
 * </pre>
 *
 * @author sb
 */
public class ValidCandidateGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ValidAlignmentGenerator.class);
    private List<LeafSubstructure<?, ?>> queryMotif;
    private List<LeafSubstructure<?, ?>> environment;

    private List<List<LeafSubstructure<?, ?>>> acceptedElementsAtEachPosition;
    private Set<List<LeafSubstructure<?, ?>>> candidates;

    public ValidCandidateGenerator(List<LeafSubstructure<?, ?>> queryMotif, List<LeafSubstructure<?, ?>> environment) {
        this.queryMotif = queryMotif;
        this.environment = environment;
        if (environment.size() < queryMotif.size()) {
            throw new IllegalArgumentException("environment cannot be smaller than query motif");
        }
    }

    public Set<List<LeafSubstructure<?, ?>>> getValidCandidates() {
        this.acceptedElementsAtEachPosition = new ArrayList<>();
        // handle each position of query motif and each of its exchanges
        for (LeafSubstructure<?, ?> currentLeafSubstructure : this.queryMotif) {
            Set<?> typesAtPosition = currentLeafSubstructure.getContainingTypes();

            List<LeafSubstructure<?, ?>> validLeafSubstructures = this.environment.stream()
                    .filter(leafSubstructure -> typesAtPosition.contains(leafSubstructure.getFamily()))
                    .collect(Collectors.toList());

            this.acceptedElementsAtEachPosition.add(validLeafSubstructures);
        }

        logger.trace("accepted elements at each position are\n{}", this.acceptedElementsAtEachPosition.stream()
                .map(position -> position.stream()
                        .map(LeafSubstructure::toString)
                        .collect(Collectors.joining("\t", "[", "]")))
                .collect(Collectors.joining("\n")));

        this.candidates = new HashSet<>();
        this.candidates.add(new ArrayList<>());
        for (int position = 0; position < this.queryMotif.size(); position++) {
            final int currentPosition = position; // :>
            this.candidates = this.candidates.stream()
                    .flatMap(candidate -> this.acceptedElementsAtEachPosition.get(currentPosition).stream()
                            .map(acceptedElement -> {
                                List<LeafSubstructure<?, ?>> newCandidate = cloneList(candidate);
                                newCandidate.add(acceptedElement);
                                return newCandidate;
                            })
                    )
                    .filter(candidate -> candidate.stream().map(LeafSubstructure::getIdentifier).distinct().count() == currentPosition + 1)
                    // FIXME we should implement equal method
                    .collect(Collectors.toSet());
        }

        if (this.candidates.isEmpty()) {
            logger.trace("no valid candidates");
        } else {
            logger.trace("candidates are\n{}", this.candidates.stream()
                    .map(candidate -> candidate.stream()
                            .map(LeafSubstructure::toString)
                            .collect(Collectors.joining("\t", "[", "]")))
                    .collect(Collectors.joining("\n")));
        }

//        filterRedundantCandidates();

        return this.candidates;
    }

    /**
     * Filters redundant candidates, because combination is regardless of the order of elements.
     */
    // TODO implement this reduction of candidates
//    private void filterRedundantCandidates() {
//        Iterator<List<LeafSubstructure<?, ?>>> candidateIterator = this.candidates.iterator();
//        while (candidateIterator.hasNext()){
//            List<LeafSubstructure<?, ?>> currentCandidate = candidateIterator.next();
//            for (List<LeafSubstructure<?,?>> candidate: this.candidates){
//                if(candidate.containsAll(currentCandidate)){
//                    candidateIterator.remove();
//                    break;
//                }
//            }
//        }
////        this.candidates.removeIf(candidate -> this.candidates.stream()
////                .anyMatch(candidateToCheck -> candidateToCheck.containsAll(candidate)));
//    }

    private List<LeafSubstructure<?, ?>> cloneList(List<LeafSubstructure<?, ?>> motif) {
        return motif.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toList());
    }
}
