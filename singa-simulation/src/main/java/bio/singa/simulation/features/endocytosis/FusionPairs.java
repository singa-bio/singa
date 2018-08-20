package bio.singa.simulation.features.endocytosis;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;

/**
 * Snare pairs required for fusion event to trigger
 * @author cl
 */
public class FusionPairs extends AbstractFeature<Integer> {

    private static final String SYMBOL = "i_Pairs";

    public FusionPairs(Integer integer, FeatureOrigin featureOrigin) {
        super(integer, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
