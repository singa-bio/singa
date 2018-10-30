package bio.singa.simulation.features.variation;

import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.UpdateModule;

/**
 * @author cl
 */
public class ModuleFeatureVariationEntry  extends VariationEntry {

    private UpdateModule module;

    public ModuleFeatureVariationEntry(UpdateModule module, Feature<?> feature) {
        this.module = module;
        setFeature(feature);
    }

    public UpdateModule getModule() {
        return module;
    }

    public void setModule(UpdateModule module) {
        this.module = module;
    }

    @Override
    public String toString() {
        return "Feature: " +
                " M = " + module.toString() +
                " F = " + getFeature();
    }

}
