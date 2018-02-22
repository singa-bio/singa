package de.bioforscher.singa.sequence.model;

import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructureContainer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class ProteinSequence extends AbstractSequence<AminoAcidFamily> {

    private ProteinSequence(List<AminoAcidFamily> sequence) {
        super(sequence);
    }

    public static ProteinSequence of(List<LeafSubstructure<?>> leafSubstructures) {
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
}
