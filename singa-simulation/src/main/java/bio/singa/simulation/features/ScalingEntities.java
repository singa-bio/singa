package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.MultiEntityFeature;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class ScalingEntities extends MultiEntityFeature {

    public ScalingEntities(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public static Builder of(List<ChemicalEntity> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(ChemicalEntity... entities) {
        return new Builder(Arrays.asList(entities));
    }

    public static class Builder extends AbstractFeature.Builder<List<ChemicalEntity>, ScalingEntities, Builder> {

        public Builder(List<ChemicalEntity> quantity) {
            super(quantity);
        }

        @Override
        protected ScalingEntities createObject(List<ChemicalEntity> quantity) {
            return new ScalingEntities(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
