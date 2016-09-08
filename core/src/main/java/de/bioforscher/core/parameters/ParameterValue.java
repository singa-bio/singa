package de.bioforscher.core.parameters;

public final class ParameterValue<Type extends Comparable<Type>> {

    private final Parameter<Type> parameter;
    private final Type value;

    public ParameterValue(Parameter<Type> parameter, Type value) {
        this.parameter = parameter;
        if (parameter.isInRange(value)) {
            this.value = value;
        } else {
            throw new IllegalArgumentException("Unable to assign " + value.toString() + " to the parameter " + parameter);
        }
    }

    public Parameter<Type> getParameter() {
        return this.parameter;
    }

    public Type getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString(); // parameter + " = " +
    }

}
