package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.EntityFeature;

/**
 * @author cl
 */
public class AttachedMotor extends EntityFeature {

    public AttachedMotor(ChemicalEntity entity) {
        super(entity);
    }

    public static Builder of(ChemicalEntity quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<ChemicalEntity, AttachedMotor, Builder> {

        public Builder(ChemicalEntity quantity) {
            super(quantity);
        }

        @Override
        protected AttachedMotor createObject(ChemicalEntity quantity) {
            return new AttachedMotor(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
