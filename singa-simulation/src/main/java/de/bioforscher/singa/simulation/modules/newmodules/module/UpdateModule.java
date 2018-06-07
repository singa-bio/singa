package de.bioforscher.singa.simulation.modules.newmodules.module;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.modules.model.LocalError;

import javax.measure.Quantity;
import java.util.Set;

/**
 * @author cl
 */
public interface UpdateModule {

    void calculateUpdates();

    ModuleState getState();

    void resetState();

    void scaleScalableFeatures();

    Set<Class<? extends Feature>> getRequiredFeatures();

    <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass);

    LocalError optimizeTimeStep();

    Set<ChemicalEntity> getReferencedEntities();

    default void checkFeatures() {
        for (Class<? extends Feature> featureClass : getRequiredFeatures()) {
            for (ChemicalEntity chemicalEntity : getReferencedEntities()) {
                if (!chemicalEntity.hasFeature(featureClass)) {
                    chemicalEntity.setFeature(featureClass);
                }
            }
        }
    }


}
