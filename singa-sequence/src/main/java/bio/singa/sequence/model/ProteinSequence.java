package bio.singa.sequence.model;

import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.Feature;

import java.util.HashSet;
import java.util.Set;

/**
 * @author fk
 */
public class ProteinSequence extends AbstractSequence {

    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        NucleotideSequence.availableFeatures.addAll(AbstractSequence.availableFeatures);
        availableFeatures.add(UniProtIdentifier.class);
    }

    public ProteinSequence(String sequence) {
        super(sequence);
    }

}
