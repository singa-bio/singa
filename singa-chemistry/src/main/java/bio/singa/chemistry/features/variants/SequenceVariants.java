package bio.singa.chemistry.features.variants;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;

import java.util.List;

/**
 * @author cl
 */
public class SequenceVariants extends AbstractFeature<List<SequenceVariant>> {

    public SequenceVariants(List<SequenceVariant> sequenceVariants, Evidence evidence) {
        super(sequenceVariants, evidence);
    }

}
