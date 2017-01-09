package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.StructuralFamily;
import de.bioforscher.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class constructs valid alignments for the Fit3D algorithm.
 * <p>
 * Consider Q=(K,D,E,E,H) to be the query motif. Further, we allow exchanges that are:
 * <p>
 * <pre>
 *  E(K1) = (H)
 *  E(E2) = (D,N)
 *  E(H)  = (K)
 * </pre>
 * <p>
 * If we now consider T=(H,E,N,D,H) to be a target of unknown order to which Q should be matched,
 * there are exactly two valid alignments:
 * <p>
 * <pre>
 *  Q =   K | D | E | E | H
 *  T =   H1| D | E | N | H2
 * </pre>
 * <p>
 * <pre>
 *  Q =   K | D | E | E | H
 *  T =   H2| D | E | N | H1
 * </pre>
 * <p>
 *
 * @author sb
 */
public class ValidAlignmentGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ValidAlignmentGenerator.class);
    private List<LeafSubstructure<?, ?>> reference;
    private List<LeafSubstructure<?, ?>> candidate;

    private List<List<LeafSubstructure<?, ?>>> pathsThroughSecondMotif;

    public ValidAlignmentGenerator(List<LeafSubstructure<?, ?>> reference, List<LeafSubstructure<?, ?>> candidate) {
        this.reference = reference;
        this.candidate = candidate;
    }

    public List<List<Pair<LeafSubstructure<?, ?>>>> getValidAlignments() {

        // initialize paths
        this.pathsThroughSecondMotif = new ArrayList<>();
        this.pathsThroughSecondMotif.add(new ArrayList<>());

        // handle each
        for (int currentPathLength = 0; currentPathLength < this.reference.size(); currentPathLength++) {
            final int expectedLength = currentPathLength + 1;

            // because we handle this as LeafStructures we can deal with any type
            LeafSubstructure currentReference = this.reference.get(currentPathLength);
            logger.trace("iteration {}: currently handling {} of reference motif", currentPathLength, currentReference);

            // for each candidate: append it to the currently known paths
            this.pathsThroughSecondMotif = this.candidate.stream()
                    // create each possible combination of the current paths and the available candidate residues
                    //TODO: this could improve, if not already consumed residues are appended
                    .flatMap(candidateResidue -> this.pathsThroughSecondMotif.stream()
                            // clone path
                            .map(this::cloneList)
                            // append path by candidateResidue
                            .map(path -> {
                                path.add(candidateResidue);
                                return path;
                            })
                    )
                    // evaluate paths:
                    // - they are invalid, when they contain the same residue multiple times
                    // - thus, when their distinct size is smaller than the currentPathLength
                    .filter(path -> path.stream()
                            .collect(Collectors.toSet()).size() == expectedLength)
                    // - other criteria: the last residue can be paired to the currentReferenceResidue
                    .filter(path -> {
                        LeafSubstructure<?, ?> recentlyAddedResidue = path.get(path.size() - 1);
                        StructuralFamily recentlyAddedResidueFamily = recentlyAddedResidue.getFamily();
                        if (recentlyAddedResidueFamily == currentReference.getFamily()) {
                            return true;
                        }
                        return currentReference.getExchangeableTypes().contains(recentlyAddedResidueFamily);
                    })
                    .collect(Collectors.toList());
        }

        return this.pathsThroughSecondMotif.stream()
                .map(path -> IntStream.range(0, path.size())
                        .mapToObj(index -> new Pair<>(this.reference.get(index), path.get(index)))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<LeafSubstructure<?, ?>> cloneList(List<LeafSubstructure<?, ?>> motif) {
        return motif.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toList());
    }
}
