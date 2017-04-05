package de.bioforscher.singa.core.events;

public interface UpdateEventListener<Type> {

    void onEventReceived(Type event);

}
