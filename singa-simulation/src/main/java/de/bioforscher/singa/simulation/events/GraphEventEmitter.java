package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.core.events.UpdateEventEmitter;
import de.bioforscher.singa.core.events.UpdateEventListener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author cl
 */
public class GraphEventEmitter implements UpdateEventEmitter<GraphUpdatedEvent> {

    private CopyOnWriteArrayList<UpdateEventListener<GraphUpdatedEvent>> listeners;

    public GraphEventEmitter() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public CopyOnWriteArrayList<UpdateEventListener<GraphUpdatedEvent>> getListeners() {
        return listeners;
    }

}
