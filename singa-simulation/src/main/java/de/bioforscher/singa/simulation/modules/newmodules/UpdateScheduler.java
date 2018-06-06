package de.bioforscher.singa.simulation.modules.newmodules;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import de.bioforscher.singa.simulation.modules.newmodules.type.ModuleState;
import de.bioforscher.singa.simulation.modules.newmodules.type.UpdateModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;

/**
 * @author cl
 */
public class UpdateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateScheduler.class);

    private Simulation simulation;
    private List<Updatable> updatables;

    private List<UpdateModule> modules;
    private ListIterator<UpdateModule> moduleIterator;
    private UpdateModule module;

    public UpdateScheduler(Simulation simulation) {
        this.simulation = simulation;
    }

    public void nextEpoch() {
        // initialize fields
        simulation.collectUpdatables();
        updatables = simulation.getUpdatables();
        moduleIterator = modules.listIterator();
        module = moduleIterator.next();
        // until all models passed
        while (moduleIterator.hasNext()) {
            processModuleByState(module.getState());
        }
        // wrap up
        finalizeDeltas();
        modules.forEach(UpdateModule::resetState);
    }

    private void processModuleByState(ModuleState state) {
        switch (state) {
            case PENDING:
                // calculate update
                module.calculateUpdates();
                // move courser back to check state
                moduleIterator.previous();
            case SUCCEEDED:
                // calculate next module if it succeeded
                moduleIterator.next();
                break;
            case RECALCULATION_REQUIRED:
                // optimize time step
                module.optimizeTimeStep();
                // reset states
                modules.forEach(UpdateModule::resetState);
                // clear deltas that have previously been calculated
                updatables.forEach(Updatable::clearPotentialDeltas);
                // start from the beginning
                moduleIterator = modules.listIterator();
                break;
            case ERRORED:
                throw new IllegalStateException("Module " + module + " errored. Sorry.");
        }
    }

    public void rescaleParameters() {
        // rescale entity parameters
        for (ChemicalEntity entity : simulation.getChemicalEntities()) {
            entity.scaleScalableFeatures();
        }
        // rescale module parameters
    }

    public void increaseTimeStep() {
        Environment.setTimeStep(Environment.getTimeStep().multiply(1.2));
        logger.debug("Increasing time step to {}.", Environment.getTimeStep());
        rescaleParameters();
    }

    public void decreaseTimeStep() {
        Environment.setTimeStep(Environment.getTimeStep().multiply(0.8));
        logger.debug("Decreasing time step to {}.", Environment.getTimeStep());
        rescaleParameters();
    }

    private void finalizeDeltas() {
        for (Updatable updatable : updatables) {
            updatable.shiftDeltas();
        }
        // potential updates for vesicles are already set during displacement evaluation
    }

}
