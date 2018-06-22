package de.bioforscher.singa.simulation.modules.newmodules.simulation;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.modules.model.LocalError;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import de.bioforscher.singa.simulation.modules.newmodules.module.ModuleState;
import de.bioforscher.singa.simulation.modules.newmodules.module.UpdateModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;

/**
 * @author cl
 */
public class UpdateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateScheduler.class);

    /**
     * The default value where errors are considered too large and the time step is reduced.
     */
    private static final double DEFAULT_RECALCULATION_CUTOFF = 0.01;

    private Simulation simulation;
    private List<Updatable> updatables;

    private List<UpdateModule> modules;
    private ListIterator<UpdateModule> moduleIterator;
    private UpdateModule module;
    private double recalculationCutoff = DEFAULT_RECALCULATION_CUTOFF;

    private boolean timestepRescaled = true;
    private LocalError largestError;
    private int processedModules;

    public UpdateScheduler(Simulation simulation) {
        this.simulation = simulation;
        modules = simulation.getModules();
        largestError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public double getRecalculationCutoff() {
        return recalculationCutoff;
    }

    public void setRecalculationCutoff(double recalculationCutoff) {
        this.recalculationCutoff = recalculationCutoff;
    }

    public void nextEpoch() {
        // initialize fields
        simulation.collectUpdatables();
        updatables = simulation.getUpdatables();
        moduleIterator = modules.listIterator();
        // until all models passed
        module = moduleIterator.next();
        timestepRescaled = false;
        processedModules = 0;
        while (processedModules < modules.size()) {
            processModuleByState(module.getState());
        }
        logger.debug("Finished processing modules for epoch {}.", simulation.getEpoch());
        // wrap up
        finalizeDeltas();
        modules.forEach(UpdateModule::resetState);
    }

    private void processModuleByState(ModuleState state) {
        logger.debug("{} is {}", module.toString(), module.getState().name());
        switch (state) {
            case PENDING:
                // calculate update
                module.calculateUpdates();
                break;
            case SUCCEEDED:
                // continue with next module
                if (moduleIterator.hasNext()) {
                    module = moduleIterator.next();
                }
                processedModules++;
                break;
            case REQUIRING_RECALCULATION:
                // optimize time step
                LocalError currentError = module.optimizeTimeStep();
                if (currentError.getValue() > largestError.getValue()) {
                    largestError = currentError;
                }
                // reset states
                modules.forEach(UpdateModule::resetState);
                // clear deltas that have previously been calculated
                updatables.forEach(Updatable::clearPotentialDeltas);
                // start from the beginning
                moduleIterator = modules.listIterator();
                module = moduleIterator.next();
                break;
            case ERRORED:
                throw new IllegalStateException("Module " + module + " errored. Sorry.");
        }
    }

    public LocalError getLargestError() {
        return largestError;
    }

    public boolean timestepWasRescaled() {
        return timestepRescaled;
    }

    public void rescaleParameters() {
        // rescale entity parameters
        for (ChemicalEntity entity : simulation.getChemicalEntities()) {
            entity.scaleScalableFeatures();
        }
        // rescale module parameters
        for (UpdateModule module : modules) {
            module.scaleScalableFeatures();
        }
    }

    public void increaseTimeStep() {
        Environment.setTimeStep(Environment.getTimeStep().multiply(1.2));
        logger.debug("Increasing time step to {}.", Environment.getTimeStep());
        rescaleParameters();
        timestepRescaled = true;
    }

    public void decreaseTimeStep() {
        Environment.setTimeStep(Environment.getTimeStep().multiply(0.8));
        logger.debug("Decreasing time step to {}.", Environment.getTimeStep());
        rescaleParameters();
        timestepRescaled = true;
    }

    private void finalizeDeltas() {
        for (Updatable updatable : updatables) {
            updatable.shiftDeltas();
        }
        // potential updates for vesicles are already set during displacement evaluation
    }

}
