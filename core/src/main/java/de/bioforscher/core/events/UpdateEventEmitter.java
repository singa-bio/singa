package de.bioforscher.core.events;


import java.util.concurrent.CopyOnWriteArrayList;

public interface UpdateEventEmitter<EventType> {

    default void emitEvent(EventType event) {
        for (UpdateEventListener<EventType> listener : getListeners()) {
            listener.onEventReceived(event);
        }
    }

    CopyOnWriteArrayList<UpdateEventListener<EventType>> getListeners();

    default void addEventListener(UpdateEventListener<EventType> listener) {
        getListeners().add(listener);
    }

    default void removeEventListener(UpdateEventListener<EventType> listener) {
        getListeners().remove(listener);
    }

}
