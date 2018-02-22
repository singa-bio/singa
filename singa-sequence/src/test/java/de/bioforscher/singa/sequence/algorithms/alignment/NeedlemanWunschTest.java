package de.bioforscher.singa.sequence.algorithms.alignment;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.sequence.model.NucleotideSequence;
import de.bioforscher.singa.structure.algorithms.superimposition.scores.SubstitutionMatrix;
import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.oak.OakNucleotide;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fk
 */
public class NeedlemanWunschTest {

    @Test
    public void shouldComputeAlignment() {
//        GAAC
//        CAAGAC
        NucleotideSequence firstSequence = NucleotideSequence.of(Stream.of(
                new OakNucleotide(null, NucleotideFamily.GUANOSINE),
                new OakNucleotide(null, NucleotideFamily.ADENOSINE),
                new OakNucleotide(null, NucleotideFamily.ADENOSINE),
                new OakNucleotide(null, NucleotideFamily.CYTIDINE))
                .collect(Collectors.toList()));

        NucleotideSequence secondSequence = NucleotideSequence.of(Stream.of(
                new OakNucleotide(null, NucleotideFamily.CYTIDINE),
                new OakNucleotide(null, NucleotideFamily.ADENOSINE),
                new OakNucleotide(null, NucleotideFamily.ADENOSINE),
                new OakNucleotide(null, NucleotideFamily.GUANOSINE),
                new OakNucleotide(null, NucleotideFamily.ADENOSINE),
                new OakNucleotide(null, NucleotideFamily.CYTIDINE))
                .collect(Collectors.toList()));

        NeedlemanWunsch needlemanWunsch = new NeedlemanWunsch(SubstitutionMatrix.BLOSUM_45, new Pair<>(firstSequence, secondSequence));
    }
}