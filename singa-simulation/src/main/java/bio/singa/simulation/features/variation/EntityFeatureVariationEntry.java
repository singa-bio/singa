package bio.singa.simulation.features.variation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;

/**
 * @author cl
 */
public class EntityFeatureVariationEntry extends VariationEntry {

    private ChemicalEntity entity;

    public EntityFeatureVariationEntry(ChemicalEntity entity, Feature<?> feature) {
        this.entity = entity;
        setFeature(feature);
    }

    public ChemicalEntity getEntity() {
        return entity;
    }

    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "Feature: " +
                " E = " + entity.getIdentifier() +
                " F = " + getFeature();
    }

}
