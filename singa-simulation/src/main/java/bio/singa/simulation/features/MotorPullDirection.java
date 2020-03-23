package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.StringFeature;

/**
 * @author cl
 */
public class MotorPullDirection extends StringFeature {

    public static final String PLUS = "+";
    public static final String MINUS = "-";

    public MotorPullDirection(String direction) {
        super(direction);
    }

    public static Builder of(String quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<String, MotorPullDirection, Builder> {

        public Builder(String quantity) {
            super(quantity);
        }

        @Override
        protected MotorPullDirection createObject(String quantity) {
            return new MotorPullDirection(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
