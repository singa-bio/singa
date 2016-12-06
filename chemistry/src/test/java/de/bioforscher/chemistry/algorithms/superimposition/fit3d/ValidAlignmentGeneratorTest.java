package de.bioforscher.chemistry.algorithms.superimposition.fit3d;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Residue;
import de.bioforscher.chemistry.physical.families.ResidueFamily;
import de.bioforscher.core.utility.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S on 28.11.2016.
 */
public class ValidAlignmentGeneratorTest {

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
        List<List<Pair<LeafSubstructure<?, ?>>>> validAlignments = new ValidAlignmentGenerator(this.motif1, this.motif2).getValidAlignments();

        for (int i = 0; i < validAlignments.size(); i++) {
            System.out.println("printing alignment " + (i + 1));
            for(Pair<LeafSubstructure<?, ?>> validAlignment : validAlignments.get(i)) {
                System.out.println(validAlignment.getFirst() + " . " + validAlignment.getSecond());
            }
            System.out.println();
        }
    }
}