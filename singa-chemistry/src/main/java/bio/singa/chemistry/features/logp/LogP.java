package bio.singa.chemistry.features.logp;

import bio.singa.chemistry.features.FeatureRegistry;
import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;

/**
 * @author cl
 */
public class LogP extends AbstractFeature<Double> {

    public static String SYMBOL = "log_p_okt_wat";

    public LogP(Double value, Evidence featureOrigin) {
        super(value, featureOrigin);
    }

    public static void register() {
        FeatureRegistry.addProviderForFeature(LogP.class, LogPProvider.class);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
