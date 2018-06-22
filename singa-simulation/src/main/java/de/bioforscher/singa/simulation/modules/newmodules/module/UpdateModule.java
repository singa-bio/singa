package de.bioforscher.singa.simulation.modules.newmodules.module;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
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

    void resetState();

    void scaleScalableFeatures();

    Set<Class<? extends Feature>> getRequiredFeatures();

    <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass);

    void optimizeTimeStep();

    Set<ChemicalEntity> getReferencedEntities();

    void checkFeatures();

}
