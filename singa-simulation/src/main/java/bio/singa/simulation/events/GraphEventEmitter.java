package bio.singa.simulation.events;

import bio.singa.core.events.UpdateEventEmitter;
import bio.singa.core.events.UpdateEventListener;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The implementation of an {@link UpdateEventEmitter} emitting updates of the graph to all listeners.
 *
 * @author cl
 */
public class GraphEventEmitter implements UpdateEventEmitter<GraphUpdatedEvent> {

    /**
     * All registered listeners.
     */
    private CopyOnWriteArrayList<UpdateEventListener<GraphUpdatedEvent>> listeners;

    /**
     * Creates a new GraphEventEmitter.
     */
    public GraphEventEmitter() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public CopyOnWriteArrayList<UpdateEventListener<GraphUpdatedEvent>> getListeners() {
        return listeners;
    }

}
