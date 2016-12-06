package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.StructuralFamily;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates candidates for an alignment with Fit3D utilizing an adaption of the VertexAll algorithm.
 *
 * @author fk
 */
@Deprecated
public class CandidateGenerator {
    private final List<LeafSubstructure<?, ?>> queryMotif;
    private final List<LeafSubstructure<?, ?>> environment;
    private List<List<LeafSubstructure<?, ?>>> candidateLists;

    public CandidateGenerator(List<LeafSubstructure<?, ?>> queryMotif, List<LeafSubstructure<?, ?>> environment) {
        this.queryMotif = queryMotif;
        this.environment = environment;
        if (environment.size() < queryMotif.size()) {
            throw new IllegalArgumentException("environment cannot be smaller than query motif");
        }
        this.candidateLists = new ArrayList<>();
        generateCandidates();
    }

    private void generateCandidates() {
        //fill initial lists
        for (int i = 0; i < this.queryMotif.size(); i++) {
            LeafSubstructure<?, ?> currentSubstructure = this.queryMotif.get(i);
            Set<StructuralFamily> validTypesOnCurrentPosition = getContainingTypes(currentSubstructure);
            this.candidateLists.add(i, this.environment.stream()
                    .filter(leafSubstructure -> validTypesOnCurrentPosition.contains(leafSubstructure.getFamily()))
                    .collect(Collectors.toList()));
        }

        for (int i = 0; i < this.candidateLists.size(); i++) {
            List<LeafSubstructure<?, ?>> referenceList = this.candidateLists.get(i);
            int pointer = (i + 1) % this.candidateLists.size();
            System.out.println("combining list " + i + " with " + pointer);
            for (int j = pointer; j < this.candidateLists.size(); j++) {
                System.out.println("\tcombining list " + pointer + " with " + j);
            }
        }

//        List<List<LeafSubstructure<?,?>>> candidates= new ArrayList<>();
//
//        // iterate over all matching data point items
//        for (int i = 0; i < this.candidateLists.size(); i++) {
//
//            // define list one
//            List<LeafSubstructure<?,?>> listOne = this.candidateLists.get(i);
////                    System.out.println("\tL1" + listOne + " " + listOne.size());
//            outerLoop:
//            for (LeafSubstructure<?,?> leafSubstructureOne : listOne) {
//
//                List<LeafSubstructure<?,?>> candidate = new ArrayList<>();
//                candidate.add(leafSubstructureOne);
//
//                for (int j = 0; j < this.candidateLists.size() - 1; j++) {
//
//                    // determine pointer for back reference in outer loop
//                    int pointer = (j + i + 1) % this.candidateLists.size();
////                            System.out.println("comparing" + i + " with " + pointer);
//                    List<LeafSubstructure<?,?>> listTwo = this.candidateLists.get(pointer);
////                            System.out.println("\t\ti1 " + itemOne.getLabel() + " " + Arrays.toString(itemOne
//// .getCoordinates()) + " L2 " + listTwo);
//
//                    // determine closest item of list
//                    LeafSubstructure<?, ?> nextLeafSubstructure = listTwo.get(0);
//
//                    // add closest item to candidate
//                    candidate.add(nextLeafSubstructure);
//                }
//
//                // add sorted assembled candidate to list of candidate
//                candidates.add(candidate);
//            }
//        }
//        System.out.println(candidates);
    }

    private Set<StructuralFamily> getContainingTypes(LeafSubstructure<?, ?> leafSubstructure) {
        // add types
        Set<StructuralFamily> types = new HashSet<>();
        types.add(leafSubstructure.getFamily());
        types.addAll(leafSubstructure.getExchangeableTypes());
        return types;
    }
}
