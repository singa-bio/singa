package de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model;

import javax.measure.Quantity;

/**
 * Created by Christoph on 14.07.2016.
 */
public class KineticParameter<ParameterType extends Quantity<ParameterType>> {

    private KineticParameterType parameterType;
    private Quantity<ParameterType> value;

    public KineticParameter(KineticParameterType parameterType, Quantity<ParameterType> value) {
        this.parameterType = parameterType;
        this.value = value;
    }

    public KineticParameterType getParameterType() {
        return this.parameterType;
    }

    public void setParameterType(KineticParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public Quantity<ParameterType> getValue() {
        return this.value;
    }

    public void setValue(Quantity<ParameterType> value) {
        this.value = value;
    }
}
