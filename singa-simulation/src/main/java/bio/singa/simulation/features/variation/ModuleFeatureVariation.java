package bio.singa.simulation.features.variation;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.UpdateModule;

import javax.measure.Quantity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author cl
 */
public class ModuleFeatureVariation<FeatureType> extends Variation<FeatureType> {

    private final UpdateModule module;

    private final Class<? extends Feature<FeatureType>> featureClass;

    public ModuleFeatureVariation(UpdateModule module, Class<? extends Feature<FeatureType>> featureClass) {
        this.module = module;
        this.featureClass = featureClass;
    }

    public UpdateModule getModule() {
        return module;
    }

    public Class<? extends Feature<FeatureType>> getFeatureClass() {
        return featureClass;
    }

    @Override
    public ModuleFeatureVariationEntry create(Object featureType) {
        try {
            Constructor<? extends Feature<FeatureType>> constructor = featureClass.getConstructor(Quantity.class);
            Feature<FeatureType> feature = constructor.newInstance(featureType);
            feature.addEvidence(new Evidence(Evidence.SourceType.PREDICTION, "Variation", "Parameter variation"));
            return new ModuleFeatureVariationEntry(module, feature);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to create Feature "+featureType+".", e);
        }
    }

    @Override
    public String toString() {
        return "Feature:" +
                " E = " + module.toString() +
                " F = " + featureClass.getSimpleName();
    }
}
