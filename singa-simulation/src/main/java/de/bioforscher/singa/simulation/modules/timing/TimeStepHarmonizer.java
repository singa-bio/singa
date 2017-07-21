package de.bioforscher.singa.simulation.modules.timing;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.ScalableFeature;
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

    public TimeStepHarmonizer(Simulation simulation, Quantity<Time> initialTimeStep) {
        EnvironmentalParameters.getInstance().setTimeStep(initialTimeStep);
        rescaleParameters();
        this.simulation = simulation;
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public boolean determineHarmonicTimeStep() {
        // set initial step
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

    public void rescaleParameters() {
        for (ChemicalEntity<?> entity : this.simulation.getChemicalEntities()) {
            entity.getAllFeatures().stream()
                    .filter(feature -> feature instanceof ScalableFeature)
                    .map(feature -> (ScalableFeature) feature)
                    .forEach(ScalableFeature::scale);
        }
    }

    public void increaseTimeStep() {
        logger.trace("Increasing time step for the next epoch.");
        EnvironmentalParameters.getInstance().setTimeStep(this.currentTimeStep.multiply(1.2));
        rescaleParameters();
    }

    public void decreaseTimeStep() {
        logger.trace("Reducing time step and trying again.");
        EnvironmentalParameters.getInstance().setTimeStep(this.currentTimeStep.multiply(0.4));
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
