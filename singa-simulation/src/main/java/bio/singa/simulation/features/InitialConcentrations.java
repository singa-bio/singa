package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.QualitativeFeature;
import bio.singa.simulation.model.concentrations.InitialConcentration;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class InitialConcentrations extends QualitativeFeature<List<InitialConcentration>> {

    public InitialConcentrations(List<InitialConcentration> initialConcentrations) {
        super(initialConcentrations);
    }

    public static Builder of(List<InitialConcentration> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(InitialConcentration... entities) {
        return new Builder(Arrays.asList(entities));
    }

    public static class Builder extends AbstractFeature.Builder<List<InitialConcentration>, InitialConcentrations, Builder> {

        public Builder(List<InitialConcentration> quantity) {
            super(quantity);
        }

        @Override
        protected InitialConcentrations createObject(List<InitialConcentration> quantity) {
            return new InitialConcentrations(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
