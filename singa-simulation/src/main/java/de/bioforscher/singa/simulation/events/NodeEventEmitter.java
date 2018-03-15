package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.core.events.UpdateEventEmitter;
import de.bioforscher.singa.core.events.UpdateEventListener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author cl
 */
public class NodeEventEmitter implements UpdateEventEmitter<NodeUpdatedEvent> {

    private CopyOnWriteArrayList<UpdateEventListener<NodeUpdatedEvent>> listeners;

    public NodeEventEmitter() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public CopyOnWriteArrayList<UpdateEventListener<NodeUpdatedEvent>> getListeners() {
        return listeners;
    }

}
