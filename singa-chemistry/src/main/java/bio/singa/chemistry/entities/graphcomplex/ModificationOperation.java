package bio.singa.chemistry.entities.graphcomplex;

/**
 * @author cl
 */
public enum ModificationOperation {

    BIND("%s binds to %s at %s"),
    RELEASE("%s releases %s at %s"),
    ADD("%s is added to to %s at %s"),
    REMOVE("%s is removed from %s at %s");

    private final String descriptor;

    ModificationOperation(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getDescriptor() {
        return descriptor;
    }
}
