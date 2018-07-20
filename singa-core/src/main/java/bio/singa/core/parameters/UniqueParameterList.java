package bio.singa.core.parameters;

import java.util.ArrayList;
import java.util.List;

public class UniqueParameterList<Type extends Comparable<Type>> {

    private List<ParameterValue<Type>> parameterValues;

    public UniqueParameterList() {
        parameterValues = new ArrayList<>();
    }

    public UniqueParameterList(List<ParameterValue<Type>> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public void add(ParameterValue<Type> element) {
        parameterValues.add(element);
    }

    public List<ParameterValue<Type>> getValues() {
        return parameterValues;
    }

    public void setValues(List<ParameterValue<Type>> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public ParameterValue<Type> getValue(int index) {
        return parameterValues.get(index);
    }

    @Override
    public String toString() {
        return parameterValues.toString();
    }

    @Override
    public MixedParameterList clone() {
        MixedParameterList newSet = new MixedParameterList();
        parameterValues.forEach(newSet::add);
        return newSet;
    }

}
