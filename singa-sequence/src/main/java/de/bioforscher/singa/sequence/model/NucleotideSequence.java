package de.bioforscher.singa.sequence.model;

import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructureContainer;
import de.bioforscher.singa.structure.model.interfaces.Nucleotide;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class NucleotideSequence extends AbstractSequence<NucleotideFamily> {

    private NucleotideSequence(List<NucleotideFamily> sequence) {
        super(sequence);
    }

    public static NucleotideSequence of(List<LeafSubstructure<?>> leafSubstructures) {
        List<NucleotideFamily> sequence = leafSubstructures.stream()
                .filter(Nucleotide.class::isInstance)
                .map(Nucleotide.class::cast)
                .map(Nucleotide::getFamily)
                .collect(Collectors.toList());
        return new NucleotideSequence(sequence);
    }

    public static NucleotideSequence of(LeafSubstructureContainer leafSubstructureContainer) {
        List<NucleotideFamily> sequence = leafSubstructureContainer.getAllNucleotides().stream()
                .map(Nucleotide::getFamily)
                .collect(Collectors.toList());
        return new NucleotideSequence(sequence);
    }
}

