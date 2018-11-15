package bio.singa.sequence.model;

import bio.singa.features.identifiers.ENAAccessionNumber;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.Feature;
import bio.singa.structure.model.families.NucleotideFamily;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.LeafSubstructureContainer;
import bio.singa.structure.model.interfaces.Nucleotide;

import java.util.ArrayList;
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
        availableFeatures.add(UniProtIdentifier.class);
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

    public static NucleotideSequence of(String sequence) {
        List<NucleotideFamily> nucleotides = new ArrayList<>();
        for (char c : sequence.toCharArray()) {
            nucleotides.add(NucleotideFamily.getNucleotide(c).orElse(NucleotideFamily.UNKNOWN));
        }
        return new NucleotideSequence(nucleotides);
    }
}

