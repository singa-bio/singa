package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.chemistry.physical.proteins.Residue;
import de.bioforscher.chemistry.physical.proteins.ResidueType;
import de.bioforscher.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by S on 28.11.2016.
 */
public class ValidAlignmentGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ValidAlignmentGenerator.class);
    private List<SubStructure> reference;
    private List<SubStructure> candidate;

    private List<List<SubStructure>> pathsThroughSecondMotif;

    public List<List<Pair<SubStructure>>> getValidAlignments(List<SubStructure> reference, List<SubStructure> candidate) {
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
                    .filter(path -> path.stream().map(SubStructure::getIdentifier).distinct().count() == expectedLength)
                    // - other criteria: the last residue can be paired to the currentReferenceResidue
                    .filter(path -> {
                        Residue recentlyAddedResidue = (Residue) path.get(path.size() - 1);
                        ResidueType recentlyAddedResidueType = recentlyAddedResidue.getType();
                        if(recentlyAddedResidueType == currentReferenceResidue.getType()) {
                            return true;
                        }
                        return currentReferenceResidue.getExchangeableTypes().contains(recentlyAddedResidueType);
                    })
                    .collect(Collectors.toList());
        }

        return this.pathsThroughSecondMotif.stream()
                .map(path -> IntStream.range(0, path.size())
                                      .mapToObj(index -> new Pair<>(reference.get(index), path.get(index)))
                                      .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<SubStructure> cloneList(List<SubStructure> motif) {
        return motif.stream()
                .map(SubStructure::getCopy)
                .collect(Collectors.toList());
    }
}
