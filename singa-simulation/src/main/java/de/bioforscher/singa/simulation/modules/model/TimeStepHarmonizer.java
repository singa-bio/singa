package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public class TimeStepHarmonizer {

    private static final Logger logger = LoggerFactory.getLogger(TimeStepHarmonizer.class);

    private static final double epsilon = 0.01;

    private Simulation simulation;
    private Quantity<Time> currentTimeStep;

    private Module criticalModule;
    private LocalError largestLocalError;
    private boolean timeStepChanged;

    public TimeStepHarmonizer(Simulation simulation, Quantity<Time> initialTimeStep) {
        EnvironmentalParameters.getInstance().setTimeStep(initialTimeStep);
        this.simulation = simulation;
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public boolean determineHarmonicTimeStep() {
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        // set initial step
        this.currentTimeStep = EnvironmentalParameters.getInstance().getTimeStep();
        rescaleParameters();
        // request local error for each module
        executeAllModules();
        // optimize simulation for the largest local error
        optimizeTimeStep();
        // if time step changed
        if (this.timeStepChanged) {
            // clear previously assigned deltas
            for (BioNode bioNode : this.simulation.getGraph().getNodes()) {
                bioNode.clearPotentialDeltas();
            }
            // update deltas
            executeAllModules();
        }
        // shift potential deltas to true deltas
        finalizeDeltas();
        // logger.info("finalized time step {}.", currentTimeStep);
        return this.timeStepChanged;
    }

    private void executeAllModules() {
        for (Module module : this.simulation.getModules()) {
            // determine deltas and corresponding local errors
            module.determineAllDeltas();
            // determine critical node and module and chemical entity and local error
            LocalError largestLocalError = module.getLargestLocalError();
            module.resetLargestLocalError();
            examineLocalError(module, largestLocalError);
        }
    }

    private void finalizeDeltas() {
        for (BioNode node : this.simulation.getGraph().getNodes()) {
            node.shiftDeltas();
        }
    }

    private void examineLocalError(Module module, LocalError localError) {
        if (this.largestLocalError.getValue() < localError.getValue()) {
            this.largestLocalError = localError;
            this.criticalModule = module;
        }
    }

    private void optimizeTimeStep() {
        double localError = largestLocalError.getValue();
        this.timeStepChanged = false;
        boolean errorIsTooLarge = evaluateLocalError(largestLocalError.getValue());
        while (errorIsTooLarge) {
            // set full time step
            this.currentTimeStep = EnvironmentalParameters.getInstance().getTimeStep();
            // determine biggest local error
            localError = this.criticalModule.determineDeltasForNode(this.largestLocalError.getNode()).getValue();
            // logger.info("Current local error is {}",localError);
            this.criticalModule.resetLargestLocalError();
            // evaluate error by increasing or decreasing time step
            errorIsTooLarge = evaluateLocalError(localError);
        }
        logger.debug("Optimized local error was {}.", localError);
    }

    public void rescaleParameters() {
        for (ChemicalEntity<?> entity : this.simulation.getChemicalEntities()) {
            entity.scaleScalableFeatures();
        }
        for (Module module : this.simulation.getModules()) {
            if (module instanceof Featureable) {
                ((Featureable) module).scaleScalableFeatures();
            }
        }
    }

    public void increaseTimeStep() {
        logger.trace("Increasing time step for the next epoch.");
        EnvironmentalParameters.getInstance().setTimeStep(this.currentTimeStep.multiply(1.2));
        rescaleParameters();
    }

    public void decreaseTimeStep() {
        logger.trace("Reducing time step and trying again.");
        EnvironmentalParameters.getInstance().setTimeStep(this.currentTimeStep.multiply(0.8));
        rescaleParameters();
    }

    private boolean evaluateLocalError(double localError) {
        // determine whether to increase or reduce time step size
        if (localError > epsilon) {
            decreaseTimeStep();
            this.timeStepChanged = true;
            return true;
        }
        return false;
    }


}
