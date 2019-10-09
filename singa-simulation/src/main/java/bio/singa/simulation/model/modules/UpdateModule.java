package bio.singa.simulation.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.Collection;
import java.util.Set;

/**
 * @author cl
 */
public interface UpdateModule extends Runnable {

    String getIdentifier();
    ModuleState getState();
    void setSimulation(Simulation simulation);

    Set<ChemicalEntity> getReferencedChemicalEntities();
    Set<Class<? extends Feature>> getRequiredFeatures();
    void checkFeatures();
    void setFeature(Feature<?> feature);
    Collection<Feature<?>> getFeatures();

    void initialize();
    void reset();
    void onReset();
    void onCompletion();

}
