package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.branches.BranchSubstructure;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.model.Exchangeable;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.graphs.model.GenericGraph;
import de.bioforscher.mathematics.graphs.model.GenericNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class AlignmentGenerator {

    private final int iterationDepth;
    private GenericGraph<Pair<LeafSubstructure<?, ?>>> alignmentGraph;
    private List<LeafSubstructure<?, ?>> reference;
    private List<LeafSubstructure<?, ?>> candidate;
    //    private List<List<Pair<BranchSubstructure>>> validPaths;
    private List<List<BranchSubstructure>> validPaths;


    public AlignmentGenerator(List<LeafSubstructure<?, ?>> reference, List<LeafSubstructure<?, ?>> candidate) {
        this.reference = reference;
        this.candidate = candidate;

//        this.validPaths.add(new ArrayList<>());
//        this.validPaths = new ArrayList<>();
        this.iterationDepth = 0;
        this.alignmentGraph = new GenericGraph<>();
        computeNextIteration(this.iterationDepth, new ArrayList<>());
//        compute(0);
//        System.out.println();
    }

    private boolean isValidAlignment(Pair<LeafSubstructure<?, ?>> subStructurePair, int iterationDepth) {
        System.out.println("eval:" + iterationDepth);

        Exchangeable res1 = subStructurePair.getFirst();
        Exchangeable res2 = subStructurePair.getSecond();
        System.out.println("id:" + res1.getFamily().equals(res2.getFamily()));
        if (res1.getFamily().equals(res2.getFamily()))
            return true;

        System.out.println("ex:" + res1.getExchangeableTypes().contains(res2.getFamily()));
        if (res1.getExchangeableTypes().contains(res2.getFamily()))
            return res1.getExchangeableTypes().contains(res2.getFamily());
//            return ((Residue) subStructurePair.getFirst())
//                    .getExchangeableTypes().contains(((Residue) subStructurePair.getSecond()).getFamily());

        if (iterationDepth != -1) {
            if (subStructurePair.getSecond().equals(this.candidate.get(iterationDepth))) {
                System.out.println("re:true");
                return false;
            }
        }

        throw new IllegalArgumentException("can not dsdsd");
    }

//    private void compute(int iterationDepth) {
//        for (BranchSubstructure candidateElement : this.candidate) {
//            List<BranchSubstructure> currentPath = this.validPaths.get(iterationDepth);
//            currentPath.add(candidateElement);
//            this.validPaths.add(currentPath);
//        }
//        if (iterationDepth != this.reference.size())
//            compute(iterationDepth + 1);
//    }

    public void computeNextIteration(int iterationDepth, List<Pair<LeafSubstructure<?, ?>>> lastValids) {

        LeafSubstructure<?, ?> referenceElement = this.reference.get(iterationDepth);
        List<Pair<LeafSubstructure<?, ?>>> currentValids = this.candidate.stream()
                .map(candidateElement -> new Pair<>(referenceElement, candidateElement))
                .peek(subStructurePair -> System.out.println("(" + subStructurePair.getFirst() + "," + subStructurePair.getSecond() + ")"))
                .filter(subStructurePair -> isValidAlignment(subStructurePair, iterationDepth))
                .collect(Collectors.toList());

        currentValids.forEach(pair -> System.out.println("filtered(" + pair.getFirst() + "," + pair.getSecond() + ")"));
        for (Pair<LeafSubstructure<?, ?>> pair : currentValids) {
            this.alignmentGraph.addNode(new GenericNode<>(this.alignmentGraph.nextNodeIdentifier(), pair));
        }
        System.out.println();
        if (iterationDepth != this.reference.size())
            computeNextIteration(iterationDepth + 1, currentValids);
    }
}
