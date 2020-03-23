package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.MultiEntityFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author cl
 */
public class Cargoes extends MultiEntityFeature {

    public Cargoes(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public static Builder of(List<ChemicalEntity> quantity) {
        return new Builder(quantity);
    }

    public static Builder of(Collection<ChemicalEntity> quantity) {
        return new Builder(new ArrayList<>(quantity));
    }

    public static Builder of(ChemicalEntity... entities) {
        return new Builder(Arrays.asList(entities));
    }

    public static class Builder extends AbstractFeature.Builder<List<ChemicalEntity>, Cargoes, Builder> {

        public Builder(List<ChemicalEntity> quantity) {
            super(quantity);
        }

        @Override
        protected Cargoes createObject(List<ChemicalEntity> quantity) {
            return new Cargoes(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
