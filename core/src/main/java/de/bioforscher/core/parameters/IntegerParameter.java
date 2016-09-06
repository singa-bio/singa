package de.bioforscher.core.parameters;

public final class IntegerParameter implements Parameter<Integer> {

    private final String name;
    private final Integer minimalValue;
    private final Integer maximalValue;

    public IntegerParameter(String name, int lowerBound, int upperBound) {
        this.name = name;
        this.minimalValue = lowerBound;
        this.maximalValue = upperBound;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getLowerBound() {
        return this.minimalValue;
    }

    @Override
    public Integer getUpperBound() {
        return this.maximalValue;
    }

    @Override
    public String toString() {
        return "Parameter (Integer) " + name + " [" + minimalValue + " ... " + maximalValue + "]";
    }

}
