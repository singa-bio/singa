package de.bioforscher.singa.simulation.modules.newmodules.module;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.layer.SpatialDelta;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import de.bioforscher.singa.simulation.modules.model.VesicleModule;
import de.bioforscher.singa.simulation.modules.newmodules.simulation.Simulation;
import de.bioforscher.singa.simulation.modules.newmodules.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.bioforscher.singa.simulation.modules.newmodules.module.ModuleState.*;

/**
 * @author cl
 */
public class DisplacementBasedModule implements UpdateModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(VesicleModule.class);

    private static final double DEFAULT_DISPLACEMENT_CUTOFF_FACTOR = 1.0/5.0;

    /**
     * The simulation.
     */
    private Simulation simulation;

    /**
     * The functions that are applied with each epoch.
     */
    private final Map<Function<Vesicle, SpatialDelta>, Predicate<Vesicle>> deltaFunctions;

    private ModuleFeatureManager featureManager;
    private ModuleState state;
    private UpdateScheduler updateScheduler;
    private Set<ChemicalEntity> referencedChemicalEntities;

    private double displacementCutoffFactor = DEFAULT_DISPLACEMENT_CUTOFF_FACTOR;
    private double displacementCutoff;

    public DisplacementBasedModule() {
        deltaFunctions = new HashMap<>();
        displacementCutoff = Environment.convertSystemToSimulationScale(Environment.getNodeDistance().multiply(displacementCutoffFactor));
        referencedChemicalEntities = new HashSet<>();
        featureManager = new ModuleFeatureManager();
        state = PENDING;
    }

    public void addDeltaFunction(Function<Vesicle, SpatialDelta> deltaFunction, Predicate<Vesicle> predicate) {
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
        for (Map.Entry<Function<Vesicle, SpatialDelta>, Predicate<Vesicle>> entry : deltaFunctions.entrySet()) {
            // test predicate
            if (entry.getValue().test(vesicle)) {
                SpatialDelta spatialDelta = entry.getKey().apply(vesicle);
                logDelta(vesicle, spatialDelta);
                vesicle.addPotentialSpatialDelta(spatialDelta);
            }
        }
    }

    private void logDelta(Vesicle vesicle, SpatialDelta delta) {
        logger.trace("Displacement delta for {} at {} is {}",
                vesicle.getStringIdentifier(),
                vesicle.getPosition(),
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
            updateScheduler.decreaseTimeStep();
            calculateUpdates();
        }
    }

    private void evaluateModuleState() {
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            Vector2D displacement = vesicle.getSpatialDelta(this).getDeltaVector();
            double length = displacement.getMagnitude();
            if (length > displacementCutoff) {
                logger.trace("Recalculation required for simulation displacement magnitude {}.", length);
                state = REQUIRING_RECALCULATION;
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
    public Set<ChemicalEntity> getReferencedEntities() {
        return referencedChemicalEntities;
    }
}
