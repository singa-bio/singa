package bio.singa.core.parameters;

import java.util.ArrayList;
import java.util.List;

public class MixedParameterList {

    private List<ParameterValue<?>> parameterValues;

    public MixedParameterList() {
        parameterValues = new ArrayList<>();
    }

    public MixedParameterList(List<ParameterValue<?>> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void add(ParameterValue<?> element) {
        parameterValues.add(element);
    }

    public List<ParameterValue<?>> getValues() {
        return parameterValues;
    }

    public void setValues(List<ParameterValue<?>> parameterValues) {
        this.parameterValues = parameterValues;
    }

    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> ParameterValue<T> getValue(int index, Class<T> typeClass) {
        return (ParameterValue<T>) parameterValues.get(index);
    }

    @Override
    public String toString() {
        return parameterValues.toString().replace("[", "").replace("]", "");
    }

    @Override
    public MixedParameterList clone() {
        MixedParameterList newSet = new MixedParameterList();
        parameterValues.forEach(newSet::add);
        return newSet;
    }

}
