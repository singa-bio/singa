package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public class TimeStepHarmonizer {

    private static final Logger logger = LoggerFactory.getLogger(TimeStepHarmonizer.class);
    private final Simulation simulation;
    private double epsilon = 0.01;
    private Quantity<Time> currentTimeStep;

    private Module criticalModule;
    private LocalError largestLocalError;
    private boolean timeStepChanged;

    public TimeStepHarmonizer(Simulation simulation, Quantity<Time> initialTimeStep) {
        EnvironmentalParameters.getInstance().setTimeStep(initialTimeStep);
        this.simulation = simulation;
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public boolean step() {
        // TODO optimize the number of times the parameters have to be rescaled (only if time step has changed)
        // TODO optimize the increasing of the time step (only when the error is very small, not every time it was good)
        // set initial step
        currentTimeStep = EnvironmentalParameters.getInstance().getTimeStep();
        rescaleParameters();
        // request local error for each module
        executeAllModules();
        // optimize simulation for the largest local error
        // System.out.println(largestLocalError+" for "+criticalModule.getClass().getSimpleName()+" at "+this.simulation.getElapsedTime()+" for ts of "+this.currentTimeStep);
        do {
            optimizeTimeStep();
            // if time step changed
            if (timeStepChanged) {
                // clear previously assigned deltas
                for (AutomatonNode bioNode : simulation.getGraph().getNodes()) {
                    bioNode.clearPotentialDeltas();
                }
                // update deltas
                executeAllModules();
            }
            // System.out.println(largestLocalError+" for "+criticalModule.getClass().getSimpleName()+" at "+this.simulation.getElapsedTime()+" for ts of "+this.currentTimeStep);
        } while (largestLocalError.getValue() > epsilon);
        // shift potential deltas to true deltas
        finalizeDeltas();

        return timeStepChanged;
    }

    private void executeAllModules() {
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        for (Module module : simulation.getModules()) {
            // determine deltas and corresponding local errors
            module.determineAllDeltas();
            // determine critical node and module and chemical entity and local error
            LocalError largestLocalError = module.getLargestLocalError();
            module.resetLargestLocalError();
            examineLocalError(module, largestLocalError);
        }
    }

    private void finalizeDeltas() {
        for (AutomatonNode node : simulation.getGraph().getNodes()) {
            node.shiftDeltas();
        }
    }

    private void examineLocalError(Module module, LocalError localError) {
        if (largestLocalError.getValue() < localError.getValue()) {
            largestLocalError = localError;
            criticalModule = module;
        }
    }

    private void optimizeTimeStep() {
        double localError = largestLocalError.getValue();
        timeStepChanged = false;
        boolean errorIsTooLarge = tryToDecreaseTimeStep(largestLocalError.getValue());
        while (errorIsTooLarge) {
            // set full time step
            currentTimeStep = EnvironmentalParameters.getInstance().getTimeStep();
            // determine biggest local error
            localError = criticalModule.determineDeltasForNode(largestLocalError.getNode()).getValue();
            // logger.info("Current local error is {}",localError);
            criticalModule.resetLargestLocalError();
            // evaluate error by increasing or decreasing time step
            errorIsTooLarge = tryToDecreaseTimeStep(localError);
        }
        logger.debug("Optimized local error was {}.", localError);
    }

    public void rescaleParameters() {
        for (ChemicalEntity<?> entity : simulation.getChemicalEntities()) {
            entity.scaleScalableFeatures();
        }
        for (Module module : simulation.getModules()) {
            if (module instanceof Featureable) {
                ((Featureable) module).scaleScalableFeatures();
            }
        }
    }

    public void increaseTimeStep() {
        logger.trace("Increasing time step for the next epoch.");
        EnvironmentalParameters.getInstance().setTimeStep(currentTimeStep.multiply(1.2));
        rescaleParameters();
    }

    public void decreaseTimeStep() {
        logger.trace("Reducing time step and trying again.");
        EnvironmentalParameters.getInstance().setTimeStep(currentTimeStep.multiply(0.8));
        rescaleParameters();
    }

    private boolean tryToDecreaseTimeStep(double localError) {
        // determine whether to increase or reduce time step size
        if (localError > epsilon) {
            decreaseTimeStep();
            timeStepChanged = true;
            return true;
        }
        return false;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }
}
