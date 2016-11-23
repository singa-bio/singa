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
        Residue motif2Asparagine1 = new Residue(8, ResidueType.ARGININE);
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

        // create pairs of substructures
        List<Pair<SubStructure>> initialPairs =
                IntStream.range(0, this.motif1.size())
                        .mapToObj(i -> new Pair<>(this.motif1.get(0), this.motif2.get(i)))
                        .filter(this::isValidAlignment).collect(Collectors.toList());

        GenericGraph<Pair<SubStructure>> alignmentGraph = new GenericGraph<>();

        for (int i = 0; i < initialPairs.size(); i++) {
            alignmentGraph.addNode(new GenericNode<>(i, initialPairs.get(i)));
        }

        alignmentGraph.addEdgeBetween(alignmentGraph.getNode(0),alignmentGraph.getNode(1));
        System.out.println();

    }

    private boolean isValidAlignment(Pair<SubStructure> subStructurePair) {

        if (subStructurePair.getFirst() instanceof Exchangeable && subStructurePair.getSecond() instanceof Exchangeable) {
            Exchangeable res1 = (Exchangeable) subStructurePair.getFirst();
            Exchangeable res2 = (Exchangeable) subStructurePair.getSecond();
            return res1.getExchangeableTypes().contains(res2.getType());
        }
//            return ((Residue) subStructurePair.getFirst())
//                    .getExchangeableTypes().contains(((Residue) subStructurePair.getSecond()).getType());
        else
            throw new IllegalArgumentException("can not dsdsd");
    }
}