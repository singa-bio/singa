package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.StructuralFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by S on 06.12.2016.
 */
public class ValidCandidateGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ValidAlignmentGenerator.class);
    private List<LeafSubstructure<?, ?>> queryMotif;
    private List<LeafSubstructure<?, ?>> environment;

    private List<List<LeafSubstructure<?, ?>>> acceptedElementsAtEachPosition;
    private List<List<LeafSubstructure<?, ?>>> candidates;

    public ValidCandidateGenerator(List<LeafSubstructure<?, ?>> queryMotif, List<LeafSubstructure<?, ?>> environment) {
        this.queryMotif = queryMotif;
        this.environment = environment;
        if (environment.size() < queryMotif.size()) {
            throw new IllegalArgumentException("environment cannot be smaller than query motif");
        }
    }

    public List<List<LeafSubstructure<?, ?>>> getValidCandidates() {
        this.acceptedElementsAtEachPosition = new ArrayList<>();
        // handle each position of query motif and each of its exchanges
        for(LeafSubstructure<?, ?> currentLeafSubstructure : this.queryMotif) {
            Set<StructuralFamily> typesAtPosition = getContainingTypes(currentLeafSubstructure);

            List<LeafSubstructure<?, ?>> validLeafSubstructures = this.environment.stream()
                    .filter(leafSubstructure -> typesAtPosition.contains(leafSubstructure.getFamily()))
                    .collect(Collectors.toList());

            this.acceptedElementsAtEachPosition.add(validLeafSubstructures);
        }

        System.out.println("accepted elements at each position");
        this.acceptedElementsAtEachPosition.forEach(System.out::println);

        this.candidates = new ArrayList<>();
        this.candidates.add(new ArrayList<>());
        for(int position = 0; position < this.queryMotif.size(); position++) {
            final int currentPosition = position; // :>
            this.candidates = this.candidates.stream()
                    .flatMap(candidate -> this.acceptedElementsAtEachPosition.get(currentPosition).stream()
                            .map(acceptedElement -> {
                                List<LeafSubstructure<?, ?>> newCandidate = cloneList(candidate);
                                newCandidate.add(acceptedElement);
                                return newCandidate;
                            })
                    )
                    .collect(Collectors.toList());
        }

        return this.candidates;
    }

    private Set<StructuralFamily> getContainingTypes(LeafSubstructure<?, ?> leafSubstructure) {
        // add types
        Set<StructuralFamily> types = new HashSet<>();
        types.add(leafSubstructure.getFamily());
        types.addAll(leafSubstructure.getExchangeableTypes());
        return types;
    }

    private List<LeafSubstructure<?, ?>> cloneList(List<LeafSubstructure<?, ?>> motif) {
        return motif.stream()
                .map(LeafSubstructure::getCopy)
                .collect(Collectors.toList());
    }
}
