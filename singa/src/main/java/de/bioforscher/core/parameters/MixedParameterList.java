package de.bioforscher.core.parameters;

import java.util.ArrayList;
import java.util.List;

public class MixedParameterList {

    private List<ParameterValue<?>> parameterValues;

    public MixedParameterList() {
        this.parameterValues = new ArrayList<>();
    }

    public MixedParameterList(List<ParameterValue<?>> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void add(ParameterValue<?> element) {
        this.parameterValues.add(element);
    }

    public List<ParameterValue<?>> getValues() {
        return parameterValues;
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> ParameterValue<T> getValue(int index, Class<T> typeClass) {
        return (ParameterValue<T>) this.parameterValues.get(index);
    }

    public void setValues(List<ParameterValue<?>> parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public String toString() {
        return parameterValues.toString().replace("[", "").replace("]", "");
    }

    @Override
    public MixedParameterList clone() {
        MixedParameterList newSet = new MixedParameterList();
        for (ParameterValue<?> value : parameterValues) {
            newSet.add(value);
        }
        return newSet;
    }

}
