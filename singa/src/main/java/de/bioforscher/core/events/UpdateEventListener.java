package de.bioforscher.core.events;

public interface UpdateEventListener<Type> {

    void onEventReceived(Type event);

}
