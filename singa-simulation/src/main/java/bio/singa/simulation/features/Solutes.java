package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.MultiEntityFeature;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class Solutes extends MultiEntityFeature {

    public Solutes(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public static Builder of(ChemicalEntity... entities) {
        return new Builder(Arrays.asList(entities));
    }

    public static Builder of(List<ChemicalEntity> quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<List<ChemicalEntity>, Solutes, Builder> {

        public Builder(List<ChemicalEntity> quantity) {
            super(quantity);
        }

        @Override
        protected Solutes createObject(List<ChemicalEntity> quantity) {
            return new Solutes(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
