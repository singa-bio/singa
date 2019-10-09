package bio.singa.simulation.model.modules.displacement;

import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.modules.AbstractUpdateModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static bio.singa.simulation.model.modules.concentration.ModuleState.REQUIRING_RECALCULATION;
import static bio.singa.simulation.model.modules.concentration.ModuleState.SUCCEEDED;

/**
 * @author cl
 */
public class DisplacementBasedModule extends AbstractUpdateModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DisplacementBasedModule.class);

    private static final double DEFAULT_DISPLACEMENT_CUTOFF_FACTOR = 1.0/10.0;

    private double displacementCutoffFactor = DEFAULT_DISPLACEMENT_CUTOFF_FACTOR;

    /**
     * The functions that are applied with each epoch.
     */
    private final Map<Function<Vesicle, DisplacementDelta>, Predicate<Vesicle>> deltaFunctions;

    private double displacementCutoff;

    public DisplacementBasedModule() {
        deltaFunctions = new HashMap<>();
        displacementCutoff = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace().multiply(displacementCutoffFactor));
    }

    public void addDeltaFunction(Function<Vesicle, DisplacementDelta> deltaFunction, Predicate<Vesicle> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public void onCompletion() {

    }

    @Override
    public void calculateUpdates() {
        processAllVesicles(getSimulation().getVesicleLayer().getVesicles());
        evaluateModuleState();
    }

    private void evaluateModuleState() {
        for (Vesicle vesicle : getSimulation().getVesicleLayer().getVesicles()) {
            if (vesicle.getSpatialDelta(this) != null) {
                Vector2D displacement = vesicle.getSpatialDelta(this).getDeltaVector();
                double length = displacement.getMagnitude();
                if (length > displacementCutoff) {
                    logger.trace("Recalculation required for module {} displacement magnitude {} exceeding threshold {}.", this, length, displacementCutoff);
                    setState(REQUIRING_RECALCULATION);
                    return;
                }
            }
        }
        setState(SUCCEEDED);
    }

    @Override
    public void optimizeTimeStep() {
        while (getState() == REQUIRING_RECALCULATION) {
            getSimulation().getVesicleLayer().clearUpdates();
            getSimulation().getScheduler().decreaseTimeStep();
            calculateUpdates();
        }
    }

    private void processAllVesicles(List<Vesicle> vesicles) {
        // determine deltas
        for (Vesicle vesicle : vesicles) {
            logger.trace("Determining delta for {}.", vesicle.getStringIdentifier());
            determineDeltas(vesicle);
        }
    }

    private void determineDeltas(Vesicle vesicle) {
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
                vesicle.getPosition(),
                delta.getDeltaVector());
    }

    public double getDisplacementCutoffFactor() {
        return displacementCutoffFactor;
    }

    public void setDisplacementCutoffFactor(double displacementCutoffFactor) {
        this.displacementCutoffFactor = displacementCutoffFactor;
        displacementCutoff = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace().multiply(displacementCutoffFactor));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + (getIdentifier() != null ? " " + getIdentifier() : "");
    }

}
