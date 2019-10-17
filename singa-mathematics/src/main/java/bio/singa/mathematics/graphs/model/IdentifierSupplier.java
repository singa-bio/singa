package bio.singa.mathematics.graphs.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cl
 */
public class IdentifierSupplier {

    private AtomicInteger identifier;

    public IdentifierSupplier() {
        identifier = new AtomicInteger(0);
    }

    public int getAndIncrement() {
        return identifier.getAndIncrement();
    }

    public int get() {
        return identifier.get();
    }

}
