package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author sb
 */
public class ValidAlignmentGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ValidAlignmentGenerator.class);
    private List<LeafSubstructure<?,?>> reference;
    private List<LeafSubstructure<?,?>> candidate;

    private List<List<LeafSubstructure<?,?>>> pathsThroughSecondMotif;

    public List<List<Pair<LeafSubstructure<?,?>>>> getValidAlignments(List<LeafSubstructure<?,?>> reference, List<LeafSubstructure<?,?>> candidate) {
        this.reference = reference;
        this.candidate = candidate;

        // initialize paths
        this.pathsThroughSecondMotif = new ArrayList<>();
        this.pathsThroughSecondMotif.add(new ArrayList<>());

        // handle each
        for(int currentPathLength = 0; currentPathLength < reference.size(); currentPathLength++) {
            final int expectedLength = currentPathLength + 1;
            //TODO: could break
            Residue currentReferenceResidue = (Residue) reference.get(currentPathLength);
            logger.info("iteration {}: currently handling {} of reference motif", currentPathLength, currentReferenceResidue);

            // for each candidate: append it to the currently known paths
            this.pathsThroughSecondMotif = candidate.stream()
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
                    //TODO: subStructure.getIdentifier() could easily break - however, this.equals(this.getCopy()) will evaluate to false :x
                    .filter(path -> path.stream().map(LeafSubstructure::getIdentifier).distinct().count() == expectedLength)
                    // - other criteria: the last residue can be paired to the currentReferenceResidue
                    .filter(path -> {
                        Residue recentlyAddedResidue = (Residue) path.get(path.size() - 1);
                        ResidueFamily recentlyAddedResidueFamily = recentlyAddedResidue.getFamily();
                        if(recentlyAddedResidueFamily == currentReferenceResidue.getFamily()) {
                            return true;
                        }
                        return currentReferenceResidue.getExchangeableTypes().contains(recentlyAddedResidueFamily);
                    })
                    .collect(Collectors.toList());
        }

        return this.pathsThroughSecondMotif.stream()
                .map(path -> IntStream.range(0, path.size())
                                      .mapToObj(index -> new Pair<>(reference.get(index), path.get(index)))
                                      .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<LeafSubstructure<?,?>> cloneList(List<LeafSubstructure<?,?>> motif) {
        return motif.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toList());
    }
}
