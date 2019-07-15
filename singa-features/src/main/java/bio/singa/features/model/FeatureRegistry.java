package bio.singa.features.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class FeatureRegistry {

    private static FeatureRegistry instance = getInstance();

    private List<QualitativeFeature<?>> qualitativeFeatures;
    private List<QuantitativeFeature<?>> quantitativeFeatures;
    private List<ScalableQuantitativeFeature<?>> scalableQuantitativeFeatures;

    private static FeatureRegistry getInstance() {
        if (instance == null) {
            reinitialize();
        }
        return instance;
    }

    public static void reinitialize() {
        synchronized (FeatureRegistry.class) {
            instance = new FeatureRegistry();
        }
    }

    private FeatureRegistry() {
        qualitativeFeatures = new ArrayList<>();
        quantitativeFeatures = new ArrayList<>();
        scalableQuantitativeFeatures = new ArrayList<>();
    }

    public static void addQuantitativeFeature(QuantitativeFeature<?> quantitativeFeature) {
        getInstance().quantitativeFeatures.add(quantitativeFeature);
    }

    public static void addQualitativeFeature(QualitativeFeature<?> qualitativeFeature) {
        getInstance().qualitativeFeatures.add(qualitativeFeature);
    }

    public static void addScalableQuantitativeFeatures(ScalableQuantitativeFeature<?> scalableQuantitativeFeature) {
        scalableQuantitativeFeature.scale();
        getInstance().scalableQuantitativeFeatures.add(scalableQuantitativeFeature);
    }

    public static  void scale() {
        for (ScalableQuantitativeFeature<?> feature : getInstance().scalableQuantitativeFeatures) {
            feature.scale();
        }
    }

    public static void scale(double factor) {
        for (ScalableQuantitativeFeature<?> feature : getInstance().scalableQuantitativeFeatures) {
            feature.scale(factor);
        }
    }

}
