package bio.singa.core.parameters;

public final class IntegerParameter implements Parameter<Integer> {

    private final String name;
    private final Integer minimalValue;
    private final Integer maximalValue;

    public IntegerParameter(String name, int lowerBound, int upperBound) {
        this.name = name;
        minimalValue = lowerBound;
        maximalValue = upperBound;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getLowerBound() {
        return minimalValue;
    }

    @Override
    public Integer getUpperBound() {
        return maximalValue;
    }

    @Override
    public String toString() {
        return "Parameter (Integer) " + name + " [" + minimalValue + " ... " + maximalValue + "]";
    }

}
