package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.MultiStringFeature;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class WhiteListVesicleStates extends MultiStringFeature {

    public WhiteListVesicleStates(List<String> vesicleStates) {
        super(vesicleStates);
    }

    public static Builder of(List<String> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(String... entities) {
        return new Builder(Arrays.asList(entities));
    }

    public static class Builder extends AbstractFeature.Builder<List<String>, WhiteListVesicleStates, Builder> {

        public Builder(List<String> quantity) {
            super(quantity);
        }

        @Override
        protected WhiteListVesicleStates createObject(List<String> quantity) {
            return new WhiteListVesicleStates(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
