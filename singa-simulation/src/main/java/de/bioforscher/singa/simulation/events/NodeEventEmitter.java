package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.core.events.UpdateEventEmitter;
import de.bioforscher.singa.core.events.UpdateEventListener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The implementation of an {@link UpdateEventEmitter} emitting updates of specific nodes to all listeners.
 *
 * @author cl
 */
public class NodeEventEmitter implements UpdateEventEmitter<UpdatableUpdatedEvent> {

    /**
     * All registered listeners.
     */
    private CopyOnWriteArrayList<UpdateEventListener<UpdatableUpdatedEvent>> listeners;

    /**
     * Creates a new NodeEventEmitter.
     */
    public NodeEventEmitter() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public CopyOnWriteArrayList<UpdateEventListener<UpdatableUpdatedEvent>> getListeners() {
        return listeners;
    }

}
