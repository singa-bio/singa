package bio.singa.simulation.model.modules.qualitative;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.parameters.FeatureManager;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public abstract class QualitativeModule implements UpdateModule {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(QualitativeModule.class);

    /**
     * The simulation.
     */
    protected Simulation simulation;

    protected UpdateScheduler updateScheduler;

    /**
     * The functions that are applied with each epoch.
     */
    private String identifier;
    private FeatureManager featureManager;
    protected ModuleState state;
    private Set<ChemicalEntity> referencedChemicalEntities;

    public QualitativeModule() {
        referencedChemicalEntities = new HashSet<>();
        featureManager = new FeatureManager();
        state = ModuleState.PENDING;
    }

    @Override
    public void initialize() {

    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        updateScheduler = simulation.getScheduler();
    }

    @Override
    public ModuleState getState() {
        return state;
    }

    @Override
    public void resetState() {
        state = ModuleState.PENDING;
        onReset();
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return featureManager.getRequiredFeatures();
    }

    @Override
    public double getScaledFeature(Class<? extends ScalableQuantitativeFeature<?>> featureClass) {
        return featureManager.getFeature(featureClass).getScaledQuantity();
    }

    /**
     * Sets a feature.
     * @param feature The feature.
     */
    public void setFeature(Feature<?> feature) {
        featureManager.setFeature(feature);
    }

    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return featureManager.getFeature(featureTypeClass);
    }

    @Override
    public Set<ChemicalEntity> getReferencedEntities() {
        return referencedChemicalEntities;
    }

    @Override
    public void checkFeatures() {
        for (Class<? extends Feature> featureClass : getRequiredFeatures()) {
            if (featureManager.hasFeature(featureClass)) {
                Feature feature = getFeature(featureClass);
                logger.debug("Required feature {} has been set to {}.", feature.getDescriptor(), feature.getContent());
            } else {
                logger.warn("Required feature {} has not been set.", featureClass.getSimpleName());
            }
        }
    }

    public Collection<Feature<?>> getFeatures() {
        return featureManager.getAllFeatures();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + (getIdentifier() != null ? " " + getIdentifier() : "");
    }
}
