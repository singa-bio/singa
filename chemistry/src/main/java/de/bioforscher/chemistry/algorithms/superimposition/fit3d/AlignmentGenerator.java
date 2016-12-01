package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.model.Exchangeable;
import de.bioforscher.chemistry.physical.model.SubStructure;
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
    GenericGraph<Pair<SubStructure>> alignmentGraph;
    private List<SubStructure> reference;
    private List<SubStructure> candidate;
    //    private List<List<Pair<SubStructure>>> validPaths;
    private List<List<SubStructure>> validPaths;


    public AlignmentGenerator(List<SubStructure> reference, List<SubStructure> candidate) {
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

    private boolean isValidAlignment(Pair<SubStructure> subStructurePair, int iterationDepth) {
        System.out.println("eval:" + iterationDepth);
        if (subStructurePair.getFirst() instanceof Exchangeable && subStructurePair.getSecond() instanceof Exchangeable) {

            Exchangeable res1 = (Exchangeable) subStructurePair.getFirst();
            Exchangeable res2 = (Exchangeable) subStructurePair.getSecond();
            System.out.println("id:" + res1.getType().equals(res2.getType()));
            if (res1.getType().equals(res2.getType()))
                return true;
        }

        if (subStructurePair.getFirst() instanceof Exchangeable && subStructurePair.getSecond() instanceof Exchangeable) {
            Exchangeable res1 = (Exchangeable) subStructurePair.getFirst();
            Exchangeable res2 = (Exchangeable) subStructurePair.getSecond();
            System.out.println("ex:" + res1.getExchangeableTypes().contains(res2.getType()));
            return res1.getExchangeableTypes().contains(res2.getType());
        }
//            return ((Residue) subStructurePair.getFirst())
//                    .getExchangeableTypes().contains(((Residue) subStructurePair.getSecond()).getType());

        if (iterationDepth != -1) {
            if (subStructurePair.getSecond().equals(this.candidate.get(iterationDepth))) {
                System.out.println("re:true");
                return false;
            }
        }

        throw new IllegalArgumentException("can not dsdsd");
    }

//    private void compute(int iterationDepth) {
//        for (SubStructure candidateElement : this.candidate) {
//            List<SubStructure> currentPath = this.validPaths.get(iterationDepth);
//            currentPath.add(candidateElement);
//            this.validPaths.add(currentPath);
//        }
//        if (iterationDepth != this.reference.size())
//            compute(iterationDepth + 1);
//    }

    public void computeNextIteration(int iterationDepth, List<Pair<SubStructure>> lastValids) {

        SubStructure referenceElement = this.reference.get(iterationDepth);
        List<Pair<SubStructure>> currentValids = this.candidate.stream()
                .map(candidateElement -> new Pair<>(referenceElement, candidateElement))
                .peek(subStructurePair -> System.out.println("(" + subStructurePair.getFirst() + "," + subStructurePair.getSecond() + ")"))
                .filter(subStructurePair -> isValidAlignment(subStructurePair, iterationDepth))
                .collect(Collectors.toList());

        currentValids.forEach(pair -> System.out.println("filtered(" + pair.getFirst() + "," + pair.getSecond() + ")"));
        for (Pair<SubStructure> pair : currentValids) {
            alignmentGraph.addNode(new GenericNode<>(alignmentGraph.nextNodeIdentifier(), pair));
        }
        System.out.println();
        if (iterationDepth != this.reference.size())
            computeNextIteration(iterationDepth + 1, currentValids);
    }
}
