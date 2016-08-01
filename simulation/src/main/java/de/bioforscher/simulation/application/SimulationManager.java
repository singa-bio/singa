package de.bioforscher.simulation.application;

import de.bioforscher.core.events.UpdateEventEmitter;
import de.bioforscher.core.events.UpdateEventListener;
import de.bioforscher.simulation.model.GraphUpdatedEvent;
import de.bioforscher.simulation.modules.model.Simulation;
import javafx.concurrent.Task;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Christoph on 01.08.2016.
 */
public class SimulationManager extends Task<Simulation> implements UpdateEventEmitter<GraphUpdatedEvent> {

    private final Simulation simulation;
    private CopyOnWriteArrayList<UpdateEventListener<GraphUpdatedEvent>> listeners;

    public SimulationManager(Simulation simulation) {
        this.simulation = simulation;
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public Simulation getSimulation() {
        return this.simulation;
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
