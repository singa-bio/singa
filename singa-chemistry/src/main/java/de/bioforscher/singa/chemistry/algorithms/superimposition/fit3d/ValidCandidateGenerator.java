package de.bioforscher.singa.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.singa.chemistry.physical.leafes.LeafSubstructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private Set<Set<LeafSubstructure<?, ?>>> candidates;

    public ValidCandidateGenerator(List<LeafSubstructure<?, ?>> queryMotif, List<LeafSubstructure<?, ?>> environment) {
        this.queryMotif = queryMotif;
        this.environment = environment;
        if (environment.size() < queryMotif.size()) {
            throw new IllegalArgumentException("environment cannot be smaller than query motif");
        }
    }

    public Set<Set<LeafSubstructure<?, ?>>> getValidCandidates() {
        this.acceptedElementsAtEachPosition = new ArrayList<>();
        // handle each position of query motif and each of its exchanges
        for (LeafSubstructure<?, ?> currentLeafSubstructure : this.queryMotif) {
            Set<?> typesAtPosition = currentLeafSubstructure.getContainingFamilies();

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
        this.candidates.add(new HashSet<>());
        for (int position = 0; position < this.queryMotif.size(); position++) {
            final int currentPosition = position; // :>
            this.candidates = this.candidates.stream()
                    .flatMap(candidate -> this.acceptedElementsAtEachPosition.get(currentPosition).stream()
                            .map(acceptedElement -> {
                                Set<LeafSubstructure<?, ?>> newCandidate = cloneSet(candidate);
                                newCandidate.add(acceptedElement);
                                return newCandidate;
                            })
                    )
                    .filter(candidate -> new HashSet<>(candidate).size() == currentPosition + 1)
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
        return this.candidates;
    }

    /**
     * Copies each of the given {@link LeafSubstructure}s and collects them as a {@link Set}.
     *
     * @param leafSubstructures The {@link LeafSubstructure}s to be cloned.
     * @return A {@link Set} of cloned {@link LeafSubstructure}s.
     */
    private Set<LeafSubstructure<?, ?>> cloneSet(Set<LeafSubstructure<?, ?>> leafSubstructures) {
        return leafSubstructures.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toSet());
    }
}
