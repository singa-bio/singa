package bio.singa.chemistry.features.variants;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

import java.util.List;

/**
 * @author cl
 */
public class SequenceVariants extends QualitativeFeature<List<SequenceVariant>> {

    public SequenceVariants(List<SequenceVariant> sequenceVariants, Evidence evidence) {
        super(sequenceVariants, evidence);
    }

}
