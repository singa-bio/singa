package de.bioforscher.singa.sequence.model;

import de.bioforscher.singa.features.identifiers.ENAAccessionNumber;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructureContainer;
import de.bioforscher.singa.structure.model.interfaces.Nucleotide;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class NucleotideSequence extends AbstractSequence<NucleotideFamily> {

    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        NucleotideSequence.availableFeatures.addAll(AbstractSequence.availableFeatures);
        availableFeatures.add(ENAAccessionNumber.class);
    }

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

