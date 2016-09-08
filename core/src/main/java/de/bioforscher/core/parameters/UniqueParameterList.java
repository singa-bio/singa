package de.bioforscher.core.parameters;

import java.util.ArrayList;
import java.util.List;

public class UniqueParameterList<Type extends Comparable<Type>> {

    private List<ParameterValue<Type>> parameterValues;

    public UniqueParameterList() {
        this.parameterValues = new ArrayList<>();
    }

    public UniqueParameterList(List<ParameterValue<Type>> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void add(ParameterValue<Type> element) {
        this.parameterValues.add(element);
    }

    public List<ParameterValue<Type>> getValues() {
        return this.parameterValues;
    }

    public ParameterValue<Type> getValue(int index) {
        return this.parameterValues.get(index);
    }

    public void setValues(List<ParameterValue<Type>> parameterValues) {
        this.parameterValues = parameterValues;
    }

    @Override
    public String toString() {
        return this.parameterValues.toString();
    }

    @Override
    public MixedParameterList clone() {
        MixedParameterList newSet = new MixedParameterList();
        this.parameterValues.forEach(newSet::add);
        return newSet;
    }

}
