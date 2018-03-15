package de.bioforscher.singa.sequence.algorithms.alignment;

import de.bioforscher.singa.sequence.model.ProteinSequence;
import de.bioforscher.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import de.bioforscher.singa.structure.model.interfaces.Structure;
import de.bioforscher.singa.structure.parser.pdb.structures.StructureParser;
import org.junit.Test;

/**
 * @author fk
 */
public class NeedlemanWunschTest {

    @Test
    public void shouldComputeAlignment() {

        Structure first = StructureParser.pdb()
                .pdbIdentifier("1n3l")
                .chainIdentifier("A")
                .parse();

        ProteinSequence firstSequence = ProteinSequence.of(first.getAllLeafSubstructures());

        Structure second = StructureParser.pdb()
                .pdbIdentifier("1pfv")
                .chainIdentifier("A")
                .parse();

        ProteinSequence secondSequence = ProteinSequence.of(second.getAllLeafSubstructures());

        NeedlemanWunschAlignment alignment = new NeedlemanWunschAlignment(SubstitutionMatrix.BLOSUM_45, firstSequence, secondSequence);

    }
}