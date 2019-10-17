package bio.singa.simulation.model.modules;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.parameters.FeatureManager;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static bio.singa.simulation.model.modules.concentration.ModuleState.*;

/**
 * @author cl
 */
public abstract class AbstractUpdateModule implements UpdateModule {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractUpdateModule.class);

    /**
     * The identifier of this module.
     */
    private String identifier;

    /**
     * The current state of this module.
     */
    private ModuleState state;

    /**
     * The referenced simulation.
     */
    private Simulation simulation;

    /**
     * The feature manager of this module
     */
    private FeatureManager featureManager;

    /**
     * All chemical entities that might be accessed by this module.
     */
    private Set<ChemicalEntity> referencedChemicalEntities;

    public AbstractUpdateModule() {
        state = PENDING;
        identifier = getClass().getSimpleName();
        featureManager = new FeatureManager();
        referencedChemicalEntities = new HashSet<>();
    }

    @Override
    public void run() {
        UpdateScheduler scheduler = getSimulation().getScheduler();
        while (state == PENDING || state == REQUIRING_RECALCULATION) {
            switch (state) {
                case PENDING:
                    // calculate update
                    logger.debug("calculating updates for {}.", Thread.currentThread().getName());
                    calculateUpdates();
                    break;
                case REQUIRING_RECALCULATION:
                    // optimize time step
                    logger.debug("{} requires recalculation.", Thread.currentThread().getName());
                    boolean prioritizedModule = scheduler.interrupt();
                    if (prioritizedModule) {
                        optimizeTimeStep();
                    } else {
                        state = INTERRUPTED;
                    }
                    break;
            }
        }
        scheduler.getCountDownLatch().countDown();
        logger.debug("Module finished {}, latch at {}.", Thread.currentThread().getName(), scheduler.getCountDownLatch().getCount());
    }

    protected abstract void calculateUpdates();

    protected abstract void optimizeTimeStep();

    /**
     * Sets a feature.
     * @param feature The feature.
     */
    @Override
    public void setFeature(Feature<?> feature) {
        featureManager.setFeature(feature);
    }

    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return featureManager.getFeature(featureTypeClass);
    }

    public double getScaledFeature(Class<? extends ScalableQuantitativeFeature<?>> featureClass) {
        return featureManager.getFeature(featureClass).getScaledQuantity();
    }

    public Collection<Feature<?>> getFeatures() {
        return featureManager.getAllFeatures();
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return featureManager.getRequiredFeatures();
    }

    @Override
    public void checkFeatures() {
        outer:
        for (Class<? extends Feature> featureClass : getRequiredFeatures()) {
            for (Feature<?> feature : featureManager.getFeatures()) {
                if (featureClass.isInstance(feature)) {
                    logger.debug("Required feature {}: {} will be used and is set to {}.", featureClass.getSimpleName(), feature.getClass().getSimpleName(), feature.getContent());
                    continue outer;
                }
            }
            logger.warn("Required feature {} has not been set for module {}.", featureClass.getSimpleName(), getIdentifier());
        }
    }

    @Override
    public void reset() {
        state = ModuleState.PENDING;
        onReset();
    }

    /**
     * Returns the referenced simulation.
     *
     * @return The referenced simulation.
     */
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * References the simulation to this module.
     *
     * @param simulation The simulation.
     */
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Returns the identifier of this module.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of this module.
     *
     * @param identifier The identifier.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ModuleState getState() {
        return state;
    }

    public void setState(ModuleState state) {
        this.state = state;
    }

    public FeatureManager getFeatureManager() {
        return featureManager;
    }

    public void setFeatureManager(FeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    /**
     * Returns all chemical entities that might be accessed by this module.
     *
     * @return All chemical entities that might be accessed by this module.
     */
    public Set<ChemicalEntity> getReferencedChemicalEntities() {
        return referencedChemicalEntities;
    }

    /**
     * Adds a referenced chemical entity.
     *
     * @param chemicalEntity The chemical entity.
     */
    protected void addReferencedEntity(ChemicalEntity chemicalEntity) {
        referencedChemicalEntities.add(chemicalEntity);
    }

    /**
     * Adds multiple referenced chemical entities.
     *
     * @param chemicalEntities The chemical entities.
     */
    protected void addReferencedEntities(Collection<? extends ChemicalEntity> chemicalEntities) {
        referencedChemicalEntities.addAll(chemicalEntities);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractUpdateModule that = (AbstractUpdateModule) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

}
