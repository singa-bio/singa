package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.StringFeature;

/**
 * @author cl
 */
public class AppliedVesicleState extends StringFeature {

    public AppliedVesicleState(String vesicleState) {
        super(vesicleState);
    }

    public static Builder of(String quantity) {
        return new Builder(quantity);
    }
    
    public static class Builder extends AbstractFeature.Builder<String, AppliedVesicleState, Builder> {
    
        public Builder(String quantity) {
            super(quantity);
        }
    
        @Override
        protected AppliedVesicleState createObject(String quantity) {
            return new AppliedVesicleState(quantity);
        }
    
        @Override
        protected Builder getBuilder() {
            return this;
        }
    } 
    
    
}
