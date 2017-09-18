package de.bioforscher.singa.core.parameters;

public final class BooleanParameter implements Parameter<Boolean> {

    private final String name;

    public BooleanParameter(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Parameter (Boolean) " + this.name + " [true|false]";
    }

    @Override
    public Boolean getLowerBound() {
        return false;
    }

    @Override
    public Boolean getUpperBound() {
        return true;
    }

    @Override
    public boolean isInRange(Boolean value) {
        return true;
    }

}
