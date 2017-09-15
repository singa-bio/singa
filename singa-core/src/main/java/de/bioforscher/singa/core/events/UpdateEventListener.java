package de.bioforscher.singa.core.events;

/**
 * An interface for the observing part of the observer pattern. Provides the object with the function to process
 * received events.
 *
 * @param <EventType> The type pf event this listener listens to.
 */
public interface UpdateEventListener<EventType> {

    /**
     * Performs an operation based on the received event.
     * @param event The received event.
     */
    void onEventReceived(EventType event);

}
