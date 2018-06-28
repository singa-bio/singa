package de.bioforscher.singa.simulation.events;

import de.bioforscher.singa.simulation.modules.model.Updatable;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

/**
 * The Event that is emitted from the {@link NodeEventEmitter}, encapsulating a updatable.
 *
 * @author cl
 */
public class UpdatableUpdatedEvent {

    /**
     * The time the event was emitted.
     */
    private final Quantity<Time> time;

    /**
     * The encapsulated updatable.
     */
    private final Updatable updatable;

    /**
     * Creates a new NodeUpdatedEvent.
     * @param time The time the event was emitted.
     * @param updatable The encapsulated updatable.
     */
    public UpdatableUpdatedEvent(Quantity<Time> time, Updatable updatable) {
        this.time = time;
        this.updatable = updatable;
    }

    /**
     * Returns the time the event was emitted.
     * @return The time the event was emitted.
     */
    public Quantity<Time> getTime() {
        return time;
    }

    /**
     * Returns the updatable
     * @return The updatable
     */
    public Updatable getUpdatable() {
        return updatable;
    }

}
