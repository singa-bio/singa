package de.bioforscher.singa.simulation.modules.timing;

import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.modules.membranetransport.PassiveMembraneTransport;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

/**
 * @author cl
 */
public class TimeStepHarmonizer {

    private static final Logger logger = LoggerFactory.getLogger(PassiveMembraneTransport.class);

    private static final double epsilon = 0.0001;

    private Simulation simulation;
    private Quantity<Time> currentTimeStep;

    private Module criticalModule;
    private LocalError largestLocalError;
    private boolean timeStepChanged;

    public TimeStepHarmonizer(Quantity<Time> initialTimeStep) {
        EnvironmentalParameters.getInstance().setTimeStep(initialTimeStep);
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public boolean determineHarmonicTimeStep() {
        // set inital step
        this.currentTimeStep = EnvironmentalParameters.getInstance().getTimeStep();
        // request local error for each module
        executeAllModules();
        // optimize simulation for the largest local error
        optimizeTimeStep();
        // if time step changed
        if (this.timeStepChanged) {
            // update deltas
            executeAllModules();
        }
        return this.timeStepChanged;
    }

    private void executeAllModules() {
        for (Module module : this.simulation.getModules()) {
            // determine deltas and corresponding local errors
            module.determineAllDeltas();
            // determine critical node and module and chemical entity and local error
            LocalError largestLocalError = module.getLargestLocalError();
            examineLocalError(module, largestLocalError);
        }
    }

    private void examineLocalError(Module module, LocalError localError) {
        if (this.largestLocalError.getValue() < localError.getValue()) {
            this.largestLocalError = localError;
            this.criticalModule = module;
        }
    }

    private void optimizeTimeStep() {
        double localError;
        this.timeStepChanged = false;
        boolean errorIsTooLarge = true;
        while (errorIsTooLarge) {
            // set full time step
            this.currentTimeStep = EnvironmentalParameters.getInstance().getTimeStep();
            // determine biggest local error
            localError = this.criticalModule.determineDeltasForNode(this.largestLocalError.getNode()).getValue();
            // evaluate error by increasing or decreasing time step
            errorIsTooLarge = evaluateLocalError(localError);
        }

    }

    public void increateTimeStep() {
        logger.trace("Increasing time step for the epoch.");
        EnvironmentalParameters.getInstance().setTimeStep(this.currentTimeStep.multiply(1.2));
    }

    private boolean evaluateLocalError(double localError) {
        // determine whether to increase or reduce time step size
        if (localError > epsilon) {
            logger.trace("Reducing time step and trying again.");
            EnvironmentalParameters.getInstance().setTimeStep(this.currentTimeStep.multiply(0.4));
            this.timeStepChanged = true;
            return true;
        }
        return false;
    }

}
