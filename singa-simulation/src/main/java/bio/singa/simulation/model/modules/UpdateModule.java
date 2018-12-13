package bio.singa.simulation.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.simulation.model.modules.concentration.ModuleState;

import java.util.Collection;
import java.util.Set;

/**
 * @author cl
 */
public interface UpdateModule {

    void calculateUpdates();

    ModuleState getState();

    void resetState();

    Set<Class<? extends Feature>> getRequiredFeatures();

    double getScaledFeature(Class<? extends ScalableQuantitativeFeature<?>> featureClass);

    Collection<Feature<?>> getFeatures();

    void setFeature(Feature<?> feature);

    void optimizeTimeStep();

    Set<ChemicalEntity> getReferencedEntities();

    void checkFeatures();

    String getIdentifier();

    void onReset();

    void onCompletion();

}
