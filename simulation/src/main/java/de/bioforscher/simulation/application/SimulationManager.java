package de.bioforscher.simulation.application;

import de.bioforscher.core.events.UpdateEventEmitter;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.events.GraphUpdatedEvent;
import de.bioforscher.simulation.modules.model.Simulation;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Christoph on 01.08.2016.
 */
public class SimulationManager extends Task<Simulation> implements UpdateEventEmitter<GraphUpdatedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SimulationManager.class);

    private final Simulation simulation;
    private CopyOnWriteArrayList<UpdateEventListener<GraphUpdatedEvent>> listeners;

    public SimulationManager(Simulation simulation) {
        logger.debug("Initializing simulation manager ...");
        this.simulation = simulation;
        this.listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void addEventListener(UpdateEventListener<GraphUpdatedEvent> listener) {
        logger.info("Added {} to registered update listeners.", listener.getClass().getSimpleName());
        this.listeners.add(listener);
    }

    @Override
    public CopyOnWriteArrayList<UpdateEventListener<GraphUpdatedEvent>> getListeners() {
        return this.listeners;
    }

    @Override
    protected Simulation call() throws Exception {
        while (!isCancelled()) {
            this.simulation.nextEpoch();
            this.emitEvent(new GraphUpdatedEvent(this.simulation.getGraph()));
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.cancel();
            }
        }
        return this.simulation;
    }

}
