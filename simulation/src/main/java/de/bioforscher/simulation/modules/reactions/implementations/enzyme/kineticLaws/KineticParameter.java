package de.bioforscher.simulation.modules.reactions.implementations.enzyme.kineticLaws;

/**
 * Created by Christoph on 14.07.2016.
 */
public class KineticParameter {

    private KineticParameterType parameterType;
    private double value;

    public KineticParameter(KineticParameterType parameterType, double value) {
        this.parameterType = parameterType;
        this.value = value;
    }

    public KineticParameterType getParameterType() {
        return this.parameterType;
    }

    public void setParameterType(KineticParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
