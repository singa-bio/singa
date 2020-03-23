package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.EntityFeature;

/**
 * @author cl
 */
public class Transporter extends EntityFeature {

    public Transporter(ChemicalEntity entity) {
        super(entity);
    }

    public static Builder of(ChemicalEntity quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<ChemicalEntity, Transporter, Builder> {

        public Builder(ChemicalEntity quantity) {
            super(quantity);
        }

        @Override
        protected Transporter createObject(ChemicalEntity quantity) {
            return new Transporter(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
