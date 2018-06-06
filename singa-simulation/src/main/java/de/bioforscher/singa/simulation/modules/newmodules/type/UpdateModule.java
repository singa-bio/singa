package de.bioforscher.singa.simulation.modules.newmodules.type;

import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.ScalableFeature;

import javax.measure.Quantity;
import java.util.Set;

/**
 * @author cl
 */
public interface UpdateModule {

    void calculateUpdates();

    ModuleState getState();

    void setState(ModuleState state);

    default void resetState() {
        setState(ModuleState.PENDING);
    }

    void rescaleParameters();

    Set<Class<? extends Feature>> getRequiredFeatures();

    <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass);

    void optimizeTimeStep();

}
