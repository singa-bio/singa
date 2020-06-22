package bio.singa.simulation.features;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.EntityFeature;

/**
 * @author cl
 */
public class BoostMediatingEntity extends EntityFeature {

    public BoostMediatingEntity(ChemicalEntity entity) {
        super(entity);
    }

    public static Builder of(ChemicalEntity quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<ChemicalEntity, BoostMediatingEntity, Builder> {

        public Builder(ChemicalEntity quantity) {
            super(quantity);
        }

        @Override
        protected BoostMediatingEntity createObject(ChemicalEntity quantity) {
            return new BoostMediatingEntity(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
