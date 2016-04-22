package de.bioforscher.core.events;

public interface UpdateEventListener<Type> {

    void onEventRecieved(Type event);

}
