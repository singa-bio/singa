package de.bioforscher.singa.simulation.model.modules.displacement;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.modules.UpdateModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ModuleState;
import de.bioforscher.singa.simulation.model.parameters.FeatureManager;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import de.bioforscher.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.bioforscher.singa.simulation.model.modules.concentration.ModuleState.*;

/**
 * @author cl
 */
public class DisplacementBasedModule implements UpdateModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DisplacementBasedModule.class);

    private static final double DEFAULT_DISPLACEMENT_CUTOFF_FACTOR = 1.0/10.0;

    /**
     * The simulation.
     */
    protected Simulation simulation;

    /**
     * The functions that are applied with each epoch.
     */
    private final Map<Function<Vesicle, DisplacementDelta>, Predicate<Vesicle>> deltaFunctions;

    private String identifier;
    private FeatureManager featureManager;
    protected ModuleState state;
    protected UpdateScheduler updateScheduler;
    private Set<ChemicalEntity> referencedChemicalEntities;

    private double displacementCutoffFactor = DEFAULT_DISPLACEMENT_CUTOFF_FACTOR;
    private double displacementCutoff;

    public DisplacementBasedModule() {
        deltaFunctions = new HashMap<>();
        displacementCutoff = Environment.convertSystemToSimulationScale(Environment.getNodeDistance().multiply(displacementCutoffFactor));
        referencedChemicalEntities = new HashSet<>();
        featureManager = new FeatureManager();
        state = PENDING;
    }

    public void addDeltaFunction(Function<Vesicle, DisplacementDelta> deltaFunction, Predicate<Vesicle> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
    }

    @Override
    public void calculateUpdates() {
        processAllVesicles(simulation.getVesicleLayer().getVesicles());
        evaluateModuleState();
    }

    public void processAllVesicles(List<Vesicle> vesicles) {
        // determine deltas
        for (Vesicle vesicle : vesicles) {
            logger.trace("Determining delta for {}.", vesicle.getStringIdentifier());
            determineDeltas(vesicle);
        }
    }

    public void determineDeltas(Vesicle vesicle) {
        for (Map.Entry<Function<Vesicle, DisplacementDelta>, Predicate<Vesicle>> entry : deltaFunctions.entrySet()) {
            // test predicate
            if (entry.getValue().test(vesicle)) {
                DisplacementDelta spatialDelta = entry.getKey().apply(vesicle);
                logDelta(vesicle, spatialDelta);
                vesicle.addPotentialSpatialDelta(spatialDelta);
            }
        }
    }

    private void logDelta(Vesicle vesicle, DisplacementDelta delta) {
        logger.trace("Displacement delta for {} at {} is {}",
                vesicle.getStringIdentifier(),
                vesicle.getCurrentPosition(),
                delta.getDeltaVector());
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
        state = PENDING;
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

    public Collection<Feature<?>> getFeatures() {
        return featureManager.getAllFeatures();
    }

    public String listFeatures(String precedingSpaces) {
        return featureManager.listFeatures(precedingSpaces);
    }

    protected <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(ChemicalEntity entity, Class<? extends ScalableFeature<FeatureContentType>> featureClass) {
        ScalableFeature<FeatureContentType> feature = entity.getFeature(featureClass);
        return feature.getScaledQuantity();
    }

    @Override
    public void optimizeTimeStep() {
        while (state == REQUIRING_RECALCULATION) {
            simulation.getVesicleLayer().clearUpdates();
            updateScheduler.decreaseTimeStep();
            calculateUpdates();
        }
    }

    protected void evaluateModuleState() {
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            if (vesicle.getSpatialDelta(this) != null) {
                Vector2D displacement = vesicle.getSpatialDelta(this).getDeltaVector();
                double length = displacement.getMagnitude();
                if (length > displacementCutoff) {
                    logger.trace("Recalculation required for module {} displacement magnitude {} exceeding threshold.", this, length, displacementCutoff);
                    state = REQUIRING_RECALCULATION;
                    return;
                }
            }
        }
        state = SUCCEEDED;
    }

    @Override
    public void checkFeatures() {
        for (Class<? extends Feature> featureClass : getRequiredFeatures()) {
            for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
                if (!vesicle.hasFeature(featureClass)) {
                    vesicle.setFeature(featureClass);
                }
            }
        }
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getStringForProtocol() {
        return getClass().getSimpleName() + " summary:" + System.lineSeparator() +
                "  " + "primary identifier: " + getIdentifier() + System.lineSeparator() +
                "  " + "features: " + System.lineSeparator() +
                listFeatures("    ");
    }

    @Override
    public Set<ChemicalEntity> getReferencedEntities() {
        return referencedChemicalEntities;
    }
}
