package de.bioforscher.singa.chemistry.descriptive.features.logp;

import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.FeatureRegistry;

/**
 * @author cl
 */
public class LogP extends AbstractFeature<Double> {

    public static void register() {
        FeatureRegistry.addProviderForFeature(LogP.class, LogPProvider.class);
    }

    public LogP(Double value, FeatureOrigin featureOrigin) {
        super(value, featureOrigin);
    }


}
