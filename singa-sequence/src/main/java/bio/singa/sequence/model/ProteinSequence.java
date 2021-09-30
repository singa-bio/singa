package bio.singa.sequence.model;

import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.Feature;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.LeafSubstructureContainer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class ProteinSequence extends AbstractSequence<AminoAcidFamily> {

    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        NucleotideSequence.availableFeatures.addAll(AbstractSequence.availableFeatures);
        availableFeatures.add(UniProtIdentifier.class);
    }

    public ProteinSequence(List<AminoAcidFamily> sequence) {
        super(sequence);
    }

    public static ProteinSequence of(List<LeafSubstructure> leafSubstructures) {
        List<AminoAcidFamily> sequence = leafSubstructures.stream()
                .filter(AminoAcid.class::isInstance)
                .map(AminoAcid.class::cast)
                .map(AminoAcid::getFamily)
                .collect(Collectors.toList());
        return new ProteinSequence(sequence);
    }

    public static ProteinSequence of(LeafSubstructureContainer leafSubstructureContainer) {
        List<AminoAcidFamily> sequence = leafSubstructureContainer.getAllAminoAcids().stream()
                .map(AminoAcid::getFamily)
                .collect(Collectors.toList());
        return new ProteinSequence(sequence);
    }

    public static ProteinSequence of(String sequence) {
        List<AminoAcidFamily> aminoAcidList = new ArrayList<>();
        for (char c : sequence.toCharArray()) {
            aminoAcidList.add(getAminoAcidTypeByOneLetterCode(c).orElse(UNKNOWN));
        }
        return new ProteinSequence(aminoAcidList);
    }

}
