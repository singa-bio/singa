package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.StringFeature;

/**
 * @author cl
 */
public class AttachedFilament extends StringFeature {

    public AttachedFilament(String filamentType) {
        super(filamentType);
    }

    public static Builder of(String quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<String, AttachedFilament, Builder> {

        public Builder(String quantity) {
            super(quantity);
        }

        @Override
        protected AttachedFilament createObject(String quantity) {
            return new AttachedFilament(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
