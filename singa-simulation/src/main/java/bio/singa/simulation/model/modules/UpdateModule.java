package bio.singa.simulation.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.Collection;
import java.util.Set;

/**
 * @author cl
 */
public interface UpdateModule {

    String getIdentifier();
    Set<ChemicalEntity> getReferencedEntities();
    void setSimulation(Simulation simulation);

    ModuleState getState();
    void resetState();

    Set<Class<? extends Feature>> getRequiredFeatures();
    Collection<Feature<?>> getFeatures();
    double getScaledFeature(Class<? extends ScalableQuantitativeFeature<?>> featureClass);
    void setFeature(Feature<?> feature);
    void checkFeatures();

    void initialize();

    void calculateUpdates();

    void optimizeTimeStep();

    void onReset();

    void onCompletion();

}
