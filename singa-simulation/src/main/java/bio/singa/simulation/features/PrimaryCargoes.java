package bio.singa.simulation.features;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.MultiEntityFeature;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class PrimaryCargoes extends MultiEntityFeature {

    public PrimaryCargoes(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public static Builder of(List<ChemicalEntity> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(ChemicalEntity... entities) {
        return new Builder(Arrays.asList(entities));
    }

    public static class Builder extends AbstractFeature.Builder<List<ChemicalEntity>, PrimaryCargoes, Builder> {

        public Builder(List<ChemicalEntity> quantity) {
            super(quantity);
        }

        @Override
        protected PrimaryCargoes createObject(List<ChemicalEntity> quantity) {
            return new PrimaryCargoes(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
