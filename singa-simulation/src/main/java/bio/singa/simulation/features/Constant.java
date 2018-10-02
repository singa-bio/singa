package bio.singa.simulation.features;

import bio.singa.features.model.FeatureOrigin;

/**
 * @author cl
 */
public class Constant {

    private double value;
    private FeatureOrigin origin;

    public Constant(double value, FeatureOrigin origin) {
        this.value = value;
        this.origin = origin;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public FeatureOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(FeatureOrigin origin) {
        this.origin = origin;
    }

}
