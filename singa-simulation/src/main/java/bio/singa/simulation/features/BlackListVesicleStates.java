package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.MultiStringFeature;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class BlackListVesicleStates extends MultiStringFeature {

    public BlackListVesicleStates(List<String> vesicleStates) {
        super(vesicleStates);
    }

    public static Builder of(List<String> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(String... entities) {
        return new Builder(Arrays.asList(entities));
    }

    public static class Builder extends AbstractFeature.Builder<List<String>, BlackListVesicleStates, Builder> {

        public Builder(List<String> quantity) {
            super(quantity);
        }

        @Override
        protected BlackListVesicleStates createObject(List<String> quantity) {
            return new BlackListVesicleStates(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
