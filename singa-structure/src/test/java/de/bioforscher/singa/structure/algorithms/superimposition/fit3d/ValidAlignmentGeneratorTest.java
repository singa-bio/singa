package de.bioforscher.singa.structure.algorithms.superimposition.fit3d;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.core.utility.Resources;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.model.oak.OakAminoAcid;
import de.bioforscher.singa.structure.model.oak.StructuralMotif;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
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
        motif1 = new ArrayList<>();
        motif1.add(motif1Lysine1);
        motif1.add(motif1AsparticAcid1);
        motif1.add(motif1GlutamicAcid1);
        motif1.add(motif1GlutamicAcid2);
        motif1.add(motif1Histidine1);

        // compose residues 2
        AminoAcid motif2Histidine1 = new OakAminoAcid(new LeafIdentifier(6), AminoAcidFamily.HISTIDINE);
        AminoAcid motif2GlutamicAcid1 = new OakAminoAcid(new LeafIdentifier(7), AminoAcidFamily.GLUTAMIC_ACID);
        AminoAcid motif2Asparagine1 = new OakAminoAcid(new LeafIdentifier(8), AminoAcidFamily.ASPARAGINE);
        AminoAcid motif2AsparticAcid1 = new OakAminoAcid(new LeafIdentifier(9), AminoAcidFamily.ASPARTIC_ACID);
        AminoAcid motif2Histidine2 = new OakAminoAcid(new LeafIdentifier(10), AminoAcidFamily.HISTIDINE);

        // compose motif 2
        motif2 = new ArrayList<>();
        motif2.add(motif2Histidine1);
        motif2.add(motif2GlutamicAcid1);
        motif2.add(motif2Asparagine1);
        motif2.add(motif2AsparticAcid1);
        motif2.add(motif2Histidine2);

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
        correctAlignments = new ArrayList<>();
        correctAlignments.add(correctAlignment1);
        correctAlignments.add(correctAlignment2);
    }

    @Test
    public void shouldGenerateValidAlignments() {
        List<List<Pair<LeafSubstructure<?>>>> validAlignments = new ValidAlignmentGenerator(motif1,
                motif2).getValidAlignments();
        assertEquals(validAlignments.size(), correctAlignments.size());
        for (List<Pair<LeafSubstructure<?>>> validAlignment : validAlignments) {
            for (int j = 0; j < validAlignment.size(); j++) {
                Pair<LeafSubstructure<?>> alignmentPair = validAlignment.get(j);
                assertTrue(alignmentPair.getFirst().equals(validAlignment.get(j).getFirst()));
                assertTrue(alignmentPair.getSecond().equals(validAlignment.get(j).getSecond()));
            }
        }
    }

    @Test
    public void shouldGenerateValidAlignmentsWithMMTF() {

        StructuralMotif structuralMotif = StructuralMotif.fromLeafSubstructures(StructureParser.local()
                .inputStream(Resources.getResourceAsStream("motif_KDEEH.pdb"))
                .parse()
                .getAllLeafSubstructures());
        structuralMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-164"), AminoAcidFamily.HISTIDINE);
        structuralMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-247"), AminoAcidFamily.ASPARTIC_ACID);
        structuralMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-247"), AminoAcidFamily.ASPARAGINE);
        structuralMotif.addExchangeableFamily(LeafIdentifier.fromSimpleString("A-297"), AminoAcidFamily.LYSINE);


        Structure target = StructureParser.mmtf()
                .pdbIdentifier("2mnr")
                .everything()
                .parse();

        List<LeafSubstructure<?>> candidate = new ArrayList<>();
        candidate.add(target.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().toString().equals("2mnr-1-A-120"))
                .findFirst().get());
        candidate.add(target.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().toString().equals("2mnr-1-A-119"))
                .findFirst().get());
        candidate.add(target.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().toString().equals("2mnr-1-A-341"))
                .findFirst().get());
        candidate.add(target.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().toString().equals("2mnr-1-A-117"))
                .findFirst().get());
        candidate.add(target.getAllLeafSubstructures().stream()
                .filter(leafSubstructure -> leafSubstructure.getIdentifier().toString().equals("2mnr-1-A-113"))
                .findFirst().get());

        List<List<Pair<LeafSubstructure<?>>>> correctAssignment = new ArrayList<>();
        List<Pair<LeafSubstructure<?>>> firstAssignment = new ArrayList<>();
        firstAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(0), candidate.get(3)));
        firstAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(1), candidate.get(4)));
        firstAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(2), candidate.get(0)));
        firstAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(3), candidate.get(2)));
        firstAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(4), candidate.get(1)));
        correctAssignment.add(firstAssignment);

        List<Pair<LeafSubstructure<?>>> secondAssignment = new ArrayList<>();
        secondAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(0), candidate.get(3)));
        secondAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(1), candidate.get(2)));
        secondAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(2), candidate.get(0)));
        secondAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(3), candidate.get(4)));
        secondAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(4), candidate.get(1)));
        correctAssignment.add(secondAssignment);

        List<Pair<LeafSubstructure<?>>> thirdAssignment = new ArrayList<>();
        thirdAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(0), candidate.get(1)));
        thirdAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(1), candidate.get(4)));
        thirdAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(2), candidate.get(0)));
        thirdAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(3), candidate.get(2)));
        thirdAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(4), candidate.get(3)));
        correctAssignment.add(thirdAssignment);

        List<Pair<LeafSubstructure<?>>> fourthAssignment = new ArrayList<>();
        fourthAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(0), candidate.get(1)));
        fourthAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(1), candidate.get(2)));
        fourthAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(2), candidate.get(0)));
        fourthAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(3), candidate.get(4)));
        fourthAssignment.add(new Pair<>(structuralMotif.getAllLeafSubstructures().get(4), candidate.get(3)));
        correctAssignment.add(fourthAssignment);

        ValidAlignmentGenerator validAlignmentGenerator = new ValidAlignmentGenerator(structuralMotif.getAllLeafSubstructures(), candidate);
        List<List<Pair<LeafSubstructure<?>>>> validAlignments = validAlignmentGenerator.getValidAlignments();
        for (int i = 0; i < validAlignments.size(); i++) {
            List<Pair<LeafSubstructure<?>>> assignment = validAlignments.get(i);
            assertEquals(correctAssignment.get(i), assignment);
        }
    }
}