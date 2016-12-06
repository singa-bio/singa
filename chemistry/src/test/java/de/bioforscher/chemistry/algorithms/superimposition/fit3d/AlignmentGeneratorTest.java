package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.model.Exchangeable;
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
@Deprecated
public class AlignmentGeneratorTest {

    private List<LeafSubstructure<?, ?>> motif1;
    private List<LeafSubstructure<?, ?>> motif2;

    @Before
    public void setUp() {

        // compose residues 1
        Residue motif1Lysine1 = new Residue(1, ResidueFamily.LYSINE);
        Residue motif1AsparticAcid1 = new Residue(2, ResidueFamily.ASPARTIC_ACID);
        Residue motif1GlutamicAcid1 = new Residue(3, ResidueFamily.GLUTAMIC_ACID);
        Residue motif1GlutamicAcid2 = new Residue(4, ResidueFamily.GLUTAMIC_ACID);
        Residue motif1Histidine1 = new Residue(5, ResidueFamily.HISTIDINE);

        // set exchanges 1
        motif1Lysine1.addExchangeableType(ResidueFamily.HISTIDINE);
        motif1GlutamicAcid2.addExchangeableType(ResidueFamily.ASPARTIC_ACID);
        motif1GlutamicAcid2.addExchangeableType(ResidueFamily.ASPARAGINE);
        motif1Histidine1.addExchangeableType(ResidueFamily.LYSINE);

        // compose motif 1
        this.motif1 = new ArrayList<>();
        this.motif1.add(motif1Lysine1);
        this.motif1.add(motif1AsparticAcid1);
        this.motif1.add(motif1GlutamicAcid1);
        this.motif1.add(motif1GlutamicAcid2);
        this.motif1.add(motif1Histidine1);

        // compose residues 2
        Residue motif2Histidine1 = new Residue(6, ResidueFamily.HISTIDINE);
        Residue motif2GlutamicAcid1 = new Residue(7, ResidueFamily.GLUTAMIC_ACID);
        Residue motif2Asparagine1 = new Residue(8, ResidueFamily.ASPARAGINE);
        Residue motif2AsparticAcid1 = new Residue(9, ResidueFamily.ASPARTIC_ACID);
        Residue motif2Histidine2 = new Residue(10, ResidueFamily.HISTIDINE);

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
        List<LeafSubstructure<?, ?>> consumedReferenceBranchSubstructures = new ArrayList<>();
        List<LeafSubstructure<?, ?>> consumedCandidateBranchSubstructures = new ArrayList<>();

        // create initial valid pairs of substructures
        List<Pair<LeafSubstructure<?, ?>>> initialPairs =
                IntStream.range(0, this.motif1.size())
                        .mapToObj(i -> new Pair<>(this.motif1.get(0), this.motif2.get(i)))
                        .filter(this::isValidAlignment).collect(Collectors.toList());
        // store already consumed substructures
        initialPairs.forEach(pair -> {
            consumedReferenceBranchSubstructures.add(pair.getFirst());
            consumedCandidateBranchSubstructures.add(pair.getSecond());
        });

        // instantiate graph
        GenericGraph<Pair<LeafSubstructure<?, ?>>> alignmentGraph = new GenericGraph<>();

        // add initial valid pairs to graph structure
        IntStream.range(0, initialPairs.size())
                .forEach(i -> alignmentGraph.addNode(new GenericNode<>(i, initialPairs.get(i))));

        for (Pair<LeafSubstructure<?, ?>> initialPair : initialPairs) {
            System.out.println("(" + initialPair.getFirst() + "," + initialPair.getSecond() + ")");
        }
        System.out.println();

        // start with next iteration
        for (int i = 1; i < this.motif1.size(); i++) {
            LeafSubstructure<?, ?> referenceBranchSubstructure = this.motif1.get(i);
            int generatedValidPairs;
            if (i == 1) {
                generatedValidPairs = initialPairs.size();
            } else {
                generatedValidPairs = 0;
            }
            if (!consumedReferenceBranchSubstructures.contains(referenceBranchSubstructure)) {
                consumedCandidateBranchSubstructures.clear();
                System.out.println(generatedValidPairs);
                int currentValidPairs = 0;
                for (int k = 0; k < generatedValidPairs; k++) {
                    for (int j = 0; j < this.motif2.size(); j++) {
                        int pointer = (j + i) % this.motif2.size();
                        consumedCandidateBranchSubstructures.add(this.motif2.get(i - 1));
//                    System.out.println(pointer);
                        LeafSubstructure<?, ?> candidateBranchSubstructure = this.motif2.get(pointer);
                        if (!consumedCandidateBranchSubstructures.contains(candidateBranchSubstructure)) {
                            Pair<LeafSubstructure<?, ?>> currentPair = new Pair<>(referenceBranchSubstructure, candidateBranchSubstructure);
                            if (isValidAlignment(currentPair)) {
                                System.out.println("(" + referenceBranchSubstructure + "," + candidateBranchSubstructure + ")");
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

        AlignmentGenerator alignmentGenerator = new AlignmentGenerator(this.motif1, this.motif2);
    }

    private boolean isValidAlignment(Pair<LeafSubstructure<?,?>> subStructurePair) {

        if (subStructurePair.getFirst() != null && subStructurePair.getSecond() != null) {
            Exchangeable res1 = subStructurePair.getFirst();
            Exchangeable res2 = subStructurePair.getSecond();
            return res1.getExchangeableTypes().contains(res2.getFamily()) || res1.getFamily().equals(res2.getFamily());
        }
//            return ((Residue) subStructurePair.getFirst())
//                    .getExchangeableTypes().contains(((Residue) subStructurePair.getSecond()).getFamily());
        else
            throw new IllegalArgumentException("can not dsdsd");
    }
}