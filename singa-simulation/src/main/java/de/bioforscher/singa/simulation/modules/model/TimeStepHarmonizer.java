package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.layer.VesicleLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.List;

/**
 * @author cl
 */
public class TimeStepHarmonizer {

    private static final Logger logger = LoggerFactory.getLogger(TimeStepHarmonizer.class);
    private final Simulation simulation;
    private double concentrationEpsilon = 0.01;
    private boolean displacementTooLarge;
    private Quantity<Time> currentTimeStep;

    private Module criticalModule;
    private LocalError largestLocalError;

    private boolean timeStepChanged;
    private List<Updatable> updatables;
    private VesicleLayer vesicleLayer;


    public TimeStepHarmonizer(Simulation simulation) {
        Environment.setTimeStep(Environment.getTimeStep());
        this.simulation = simulation;
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        timeStepChanged = true;
        displacementTooLarge = false;
    }

    public boolean step() {
        // set initial step
        simulation.collectUpdatables();
        updatables = simulation.getUpdatables();
        vesicleLayer = simulation.getVesicleLayer();
        currentTimeStep = Environment.getTimeStep();
        if (timeStepChanged) {
            rescaleParameters();
        }

        // optimize spatial layer updates
        if (vesicleLayer != null) {
            do {
                executeAllSpatialModules();
                evaluateSpatialDisplacements();
            } while (displacementTooLarge);
        }

        // optimize concentration based updates
        executeAllConcentrationModules();
        // optimize simulation for the largest local error
        // System.out.println(largestLocalError+" for "+criticalModule.getClass().getSimpleName()+" at "+this.simulation.getElapsedTime()+" for ts of "+this.currentTimeStep);
        do {
            optimizeConcentrationTimeStep();
            // if time step changed
            if (timeStepChanged) {
                // clear previously assigned deltas
                for (Updatable updatable : updatables) {
                    updatable.clearPotentialDeltas();
                }
                // update deltas
                executeAllConcentrationModules();
            }
            // System.out.println(largestLocalError+" for "+criticalModule.getClass().getSimpleName()+" at "+this.simulation.getElapsedTime()+" for ts of "+this.currentTimeStep);
        } while (largestLocalError.getValue() > concentrationEpsilon);

        // shift potential deltas to true deltas
        finalizeDeltas();

        return timeStepChanged;
    }

    private void executeAllConcentrationModules() {
        logger.debug("Calculating deltas and errors for all concentration modules.");
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        for (Module module : simulation.getModules()) {
            logger.trace("Calculating deltas for Module {}", module.toString());
            // determine deltas and corresponding local errors
            module.determineAllDeltas(updatables);
            // determine critical node and module and chemical entity and local error
            LocalError largestLocalError = module.getLargestLocalError();
            module.resetLargestLocalError();
            examineLocalError(module, largestLocalError);
        }
    }

    private void executeAllSpatialModules() {
        logger.debug("Calculating deltas and errors for all spatial modules.");
        for (VesicleModule vesicleModule : vesicleLayer.getVesicleModules()) {
            logger.trace("Calculating deltas for Module {}", vesicleModule.toString());
            // determine deltas
            vesicleModule.determineAllDeltas(vesicleLayer.getVesicles());
        }
    }

    private void examineLocalError(Module module, LocalError localError) {
        if (largestLocalError.getValue() < localError.getValue()) {
            largestLocalError = localError;
            criticalModule = module;
        }
    }

    private void evaluateSpatialDisplacements() {
        if (!vesicleLayer.deltasAreBelowDisplacementCutoff()) {
            displacementTooLarge = true;
            decreaseTimeStep();
            vesicleLayer.clearUpdates();
        } else {
            displacementTooLarge = false;
        }
    }

    private void optimizeConcentrationTimeStep() {
        double localError = largestLocalError.getValue();
        timeStepChanged = false;
        boolean errorIsTooLarge = tryToDecreaseTimeStep(largestLocalError.getValue());
        while (errorIsTooLarge) {
            // set full time step
            currentTimeStep = Environment.getTimeStep();
            // determine biggest local error
            localError = criticalModule.determineDeltas(largestLocalError.getUpdatable()).getValue();
            // logger.info("Current local error is {}",localError);
            criticalModule.resetLargestLocalError();
            // evaluate error by increasing or decreasing time step
            errorIsTooLarge = tryToDecreaseTimeStep(localError);
        }
        logger.debug("Optimized local error for {} was {} with time step of {}.", criticalModule, localError, Environment.getTimeStep());
    }

    public LocalError getLargestLocalError() {
        return largestLocalError;
    }

    private void finalizeDeltas() {
        for (Updatable updatable : updatables) {
            updatable.shiftDeltas();
        }
        // potential updates for vesicles are already set during displacement evaluation
    }

    public void rescaleParameters() {
        for (ChemicalEntity entity : simulation.getChemicalEntities()) {
            entity.scaleScalableFeatures();
        }
        for (Module module : simulation.getModules()) {
            if (module instanceof Featureable) {
                ((Featureable) module).scaleScalableFeatures();
            }
        }
    }

    public void increaseTimeStep() {
        Environment.setTimeStep(currentTimeStep.multiply(1.2));
        logger.debug("Increasing time step to {}.", Environment.getTimeStep());
        rescaleParameters();
        timeStepChanged = true;
    }

    public void decreaseTimeStep() {
        Environment.setTimeStep(currentTimeStep.multiply(0.8));
        logger.debug("Decreasing time step to {}.", Environment.getTimeStep());
        rescaleParameters();
        timeStepChanged = true;
    }

    private boolean tryToDecreaseTimeStep(double localError) {
        // determine whether to increase or reduce time step size
        if (localError > concentrationEpsilon) {
            decreaseTimeStep();
            return true;
        }
        return false;
    }

    public double getConcentrationEpsilon() {
        return concentrationEpsilon;
    }

    public void setConcentrationEpsilon(double concentrationEpsilon) {
        this.concentrationEpsilon = concentrationEpsilon;
    }
}
