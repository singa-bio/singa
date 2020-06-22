package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.StringFeature;

/**
 * @author cl
 */
public class RequiredVesicleState extends StringFeature {

    public RequiredVesicleState(String vesicleState) {
        super(vesicleState);
    }

    public static Builder of(String quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<String, RequiredVesicleState, Builder> {

        public Builder(String quantity) {
            super(quantity);
        }

        @Override
        protected RequiredVesicleState createObject(String quantity) {
            return new RequiredVesicleState(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
