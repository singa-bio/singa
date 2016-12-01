package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.model.Exchangeable;
import de.bioforscher.chemistry.physical.model.SubStructure;
import de.bioforscher.chemistry.physical.proteins.Residue;
import de.bioforscher.chemistry.physical.proteins.ResidueType;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.graphs.model.GenericGraph;
import de.bioforscher.mathematics.graphs.model.GenericNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author fk
 */
public class AlignmentGeneratorTest {

    private List<SubStructure> motif1;
    private List<SubStructure> motif2;

    @Before
    public void setUp() {

        // compose residues 1
        Residue motif1Lysine1 = new Residue(1, ResidueType.LYSINE);
        Residue motif1AsparticAcid1 = new Residue(2, ResidueType.ASPARTIC_ACID);
        Residue motif1GlutamicAcid1 = new Residue(3, ResidueType.GLUTAMIC_ACID);
        Residue motif1GlutamicAcid2 = new Residue(4, ResidueType.GLUTAMIC_ACID);
        Residue motif1Histidine1 = new Residue(5, ResidueType.HISTIDINE);

        // set exchanges 1
        motif1Lysine1.addExchangeableType(ResidueType.HISTIDINE);
        motif1GlutamicAcid2.addExchangeableType(ResidueType.ASPARTIC_ACID);
        motif1GlutamicAcid2.addExchangeableType(ResidueType.ASPARAGINE);
        motif1Histidine1.addExchangeableType(ResidueType.LYSINE);

        // compose motif 1
        this.motif1 = new ArrayList<>();
        this.motif1.add(motif1Lysine1);
        this.motif1.add(motif1AsparticAcid1);
        this.motif1.add(motif1GlutamicAcid1);
        this.motif1.add(motif1GlutamicAcid2);
        this.motif1.add(motif1Histidine1);

        // compose residues 2
        Residue motif2Histidine1 = new Residue(6, ResidueType.HISTIDINE);
        Residue motif2GlutamicAcid1 = new Residue(7, ResidueType.GLUTAMIC_ACID);
        Residue motif2Asparagine1 = new Residue(8, ResidueType.ASPARAGINE);
        Residue motif2AsparticAcid1 = new Residue(9, ResidueType.ASPARTIC_ACID);
        Residue motif2Histidine2 = new Residue(10, ResidueType.HISTIDINE);

        // compose motif 2
        this.motif2 = new ArrayList<>();
        this.motif2.add(motif2Histidine1);
        this.motif2.add(motif2GlutamicAcid1);
        this.motif2.add(motif2Asparagine1);
        this.motif2.add(motif2AsparticAcid1);
        this.motif2.add(motif2Histidine2);
    }

    @Test
    public void shouldGenerateValidAlignments() {

        // holds the already consumed reference substructures
        List<SubStructure> consumedReferenceSubstructures = new ArrayList<>();
        List<SubStructure> consumedCandidateSubstructures = new ArrayList<>();

        // create initial valid pairs of substructures
        List<Pair<SubStructure>> initialPairs =
                IntStream.range(0, this.motif1.size())
                        .mapToObj(i -> new Pair<>(this.motif1.get(0), this.motif2.get(i)))
                        .filter(this::isValidAlignment).collect(Collectors.toList());
        // store already consumed substructures
        initialPairs.forEach(pair -> {
            consumedReferenceSubstructures.add(pair.getFirst());
            consumedCandidateSubstructures.add(pair.getSecond());
        });

        // instantiate graph
        GenericGraph<Pair<SubStructure>> alignmentGraph = new GenericGraph<>();

        // add initial valid pairs to graph structure
        IntStream.range(0, initialPairs.size())
                .forEach(i -> alignmentGraph.addNode(new GenericNode<>(i, initialPairs.get(i))));

        for (Pair<SubStructure> initialPair : initialPairs) {
            System.out.println("(" + initialPair.getFirst() + "," + initialPair.getSecond() + ")");
        }
        System.out.println();

        // start with next iteration
        for (int i = 1; i < this.motif1.size(); i++) {
            SubStructure referenceSubstructure = this.motif1.get(i);
            int generatedValidPairs;
            if (i == 1) {
                generatedValidPairs = initialPairs.size();
            } else {
                generatedValidPairs = 0;
            }
            if (!consumedReferenceSubstructures.contains(referenceSubstructure)) {
                consumedCandidateSubstructures.clear();
                System.out.println(generatedValidPairs);
                int currentValidPairs = 0;
                for (int k = 0; k < generatedValidPairs; k++) {
                    for (int j = 0; j < this.motif2.size(); j++) {
                        int pointer = (j + i) % this.motif2.size();
                        consumedCandidateSubstructures.add(this.motif2.get(i - 1));
//                    System.out.println(pointer);
                        SubStructure candidateSubstructure = this.motif2.get(pointer);
                        if (!consumedCandidateSubstructures.contains(candidateSubstructure)) {
                            Pair<SubStructure> currentPair = new Pair<>(referenceSubstructure, candidateSubstructure);
                            if (isValidAlignment(currentPair)) {
                                System.out.println("(" + referenceSubstructure + "," + candidateSubstructure + ")");
                                System.out.println(generatedValidPairs);
                                currentValidPairs++;
                            }
                        }
                    }
                }
                generatedValidPairs = currentValidPairs;
                System.out.println();
            }
        }
        System.out.println();

    }

    @Test
    public void shouldGenerateValidAlignmentsSecond() {

        AlignmentGenerator alignmentGenerator = new AlignmentGenerator(motif1, motif2);
    }

    private boolean isValidAlignment(Pair<SubStructure> subStructurePair) {

        if (subStructurePair.getFirst() instanceof Exchangeable && subStructurePair.getSecond() instanceof Exchangeable) {
            Exchangeable res1 = (Exchangeable) subStructurePair.getFirst();
            Exchangeable res2 = (Exchangeable) subStructurePair.getSecond();
            return res1.getExchangeableTypes().contains(res2.getType()) || res1.getType().equals(res2.getType());
        }
//            return ((Residue) subStructurePair.getFirst())
//                    .getExchangeableTypes().contains(((Residue) subStructurePair.getSecond()).getType());
        else
            throw new IllegalArgumentException("can not dsdsd");
    }
}