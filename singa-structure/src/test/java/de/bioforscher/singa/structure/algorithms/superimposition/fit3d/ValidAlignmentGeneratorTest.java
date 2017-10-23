package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.oak.OakAminoAcid;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author sb
 */
public class ValidAlignmentGeneratorTest {

    private List<LeafSubstructure<?>> motif1;
    private List<LeafSubstructure<?>> motif2;
    private List<List<Pair<AminoAcid>>> correctAlignments;

    @Before
    public void setUp() {

        // compose residues 1
        AminoAcid motif1Lysine1 = new OakAminoAcid(new LeafIdentifier(1), AminoAcidFamily.LYSINE);
        AminoAcid motif1AsparticAcid1 = new OakAminoAcid(new LeafIdentifier(2), AminoAcidFamily.ASPARTIC_ACID);
        AminoAcid motif1GlutamicAcid1 = new OakAminoAcid(new LeafIdentifier(3), AminoAcidFamily.GLUTAMIC_ACID);
        AminoAcid motif1GlutamicAcid2 = new OakAminoAcid(new LeafIdentifier(4), AminoAcidFamily.GLUTAMIC_ACID);
        AminoAcid motif1Histidine1 = new OakAminoAcid(new LeafIdentifier(5), AminoAcidFamily.HISTIDINE);

        // set exchanges 1
        motif1Lysine1.addExchangeableFamily(AminoAcidFamily.HISTIDINE);
        motif1GlutamicAcid2.addExchangeableFamily(AminoAcidFamily.ASPARTIC_ACID);
        motif1GlutamicAcid2.addExchangeableFamily(AminoAcidFamily.ASPARAGINE);
        motif1Histidine1.addExchangeableFamily(AminoAcidFamily.LYSINE);

        // compose motif 1
        this.motif1 = new ArrayList<>();
        this.motif1.add(motif1Lysine1);
        this.motif1.add(motif1AsparticAcid1);
        this.motif1.add(motif1GlutamicAcid1);
        this.motif1.add(motif1GlutamicAcid2);
        this.motif1.add(motif1Histidine1);

        // compose residues 2
        AminoAcid motif2Histidine1 = new  OakAminoAcid(new LeafIdentifier(6), AminoAcidFamily.HISTIDINE);
        AminoAcid motif2GlutamicAcid1 = new  OakAminoAcid(new LeafIdentifier(7), AminoAcidFamily.GLUTAMIC_ACID);
        AminoAcid motif2Asparagine1 = new  OakAminoAcid(new LeafIdentifier(8), AminoAcidFamily.ASPARAGINE);
        AminoAcid motif2AsparticAcid1 = new  OakAminoAcid(new LeafIdentifier(9), AminoAcidFamily.ASPARTIC_ACID);
        AminoAcid motif2Histidine2 = new  OakAminoAcid(new LeafIdentifier(10), AminoAcidFamily.HISTIDINE);

        // compose motif 2
        this.motif2 = new ArrayList<>();
        this.motif2.add(motif2Histidine1);
        this.motif2.add(motif2GlutamicAcid1);
        this.motif2.add(motif2Asparagine1);
        this.motif2.add(motif2AsparticAcid1);
        this.motif2.add(motif2Histidine2);

        // store correct alignments
        List<Pair<AminoAcid>> correctAlignment1 = new ArrayList<>();
        correctAlignment1.add(new Pair<>(motif1Lysine1, motif2Histidine2));
        correctAlignment1.add(new Pair<>(motif1AsparticAcid1, motif2AsparticAcid1));
        correctAlignment1.add(new Pair<>(motif1GlutamicAcid1, motif2GlutamicAcid1));
        correctAlignment1.add(new Pair<>(motif1Histidine1, motif2Histidine1));

        List<Pair<AminoAcid>> correctAlignment2 = new ArrayList<>();
        correctAlignment2.add(new Pair<>(motif1Lysine1, motif2Histidine1));
        correctAlignment2.add(new Pair<>(motif1AsparticAcid1, motif2AsparticAcid1));
        correctAlignment2.add(new Pair<>(motif1GlutamicAcid1, motif2GlutamicAcid1));
        correctAlignment2.add(new Pair<>(motif1Histidine1, motif2Histidine2));
        this.correctAlignments = new ArrayList<>();
        this.correctAlignments.add(correctAlignment1);
        this.correctAlignments.add(correctAlignment2);
    }

    @Test
    public void shouldGenerateValidAlignments() {
        List<List<Pair<LeafSubstructure<?>>>> validAlignments = new ValidAlignmentGenerator(this.motif1,
                this.motif2).getValidAlignments();
        assertEquals(validAlignments.size(), this.correctAlignments.size());
        for (List<Pair<LeafSubstructure<?>>> validAlignment : validAlignments) {
            for (int j = 0; j < validAlignment.size(); j++) {
                Pair<LeafSubstructure<?>> alignmentPair = validAlignment.get(j);
                assertTrue(alignmentPair.getFirst().equals(validAlignment.get(j).getFirst()));
                assertTrue(alignmentPair.getSecond().equals(validAlignment.get(j).getSecond()));
            }
        }
    }
}