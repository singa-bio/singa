package bio.singa.core.events;


import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An interface for the observed part of the observer pattern without the need to extend a class such as the {@link
 * java.util.Observable} class. It requires the implementing class to provide a {@link CopyOnWriteArrayList} to store
 * the {@link UpdateEventListener}.
 *
 * @param <EventType> Thy Type of Event that is emitted.
 * @author cl
 */
public interface UpdateEventEmitter<EventType> {

    /**
     * Emits an update to all listeners registered for this object.
     *
     * @param event The event to emit.
     */
    default void emitEvent(EventType event) {
        for (UpdateEventListener<EventType> listener : getListeners()) {
            listener.onEventReceived(event);
        }
    }

    /**
     * Returns the listeners.
     *
     * @return The listeners.
     */
    CopyOnWriteArrayList<UpdateEventListener<EventType>> getListeners();

    /**
     * Adds a listener.
     *
     * @param listener The listener to add.
     */
    default void addEventListener(UpdateEventListener<EventType> listener) {
        getListeners().add(listener);
    }

    /**
     * Removes a listener.
     *
     * @param listener The listener to remove.
     */
    default void removeEventListener(UpdateEventListener<EventType> listener) {
        getListeners().remove(listener);
    }

}
