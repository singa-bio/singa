package bio.singa.simulation.model.modules.qualitative;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.exceptions.FeatureUnassignableException;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableFeature;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.parameters.FeatureManager;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public abstract class QualitativeModule implements UpdateModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DisplacementBasedModule.class);

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
    public void scaleScalableFeatures() {
        featureManager.scaleScalableFeatures();
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return featureManager.getRequiredFeatures();
    }

    @Override
    public <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass) {
        return featureManager.getFeature(featureClass).getScaledQuantity();
    }

    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
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
        for (Class<? extends Feature> feature : featureManager.getRequiredFeatures()) {
            if (!featureManager.hasFeature(feature)) {
                throw new FeatureUnassignableException(toString()+" requires the "+feature+" feature");
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
    public String getStringForProtocol() {
        return getClass().getSimpleName();
    }

}
