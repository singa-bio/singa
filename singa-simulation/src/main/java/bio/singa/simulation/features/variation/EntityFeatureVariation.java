package bio.singa.simulation.features.variation;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.Evidence;

import javax.measure.Quantity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author cl
 */
public class EntityFeatureVariation<FeatureType> extends Variation<FeatureType> {

    private final ChemicalEntity entity;

    private final Class<? extends Feature<FeatureType>> featureClass;

    public EntityFeatureVariation(ChemicalEntity entity, Class<? extends Feature<FeatureType>> featureClass) {
        this.entity = entity;
        this.featureClass = featureClass;
    }

    public ChemicalEntity getEntity() {
        return entity;
    }

    public Class<? extends Feature> getFeatureClass() {
        return featureClass;
    }

    @Override
    public EntityFeatureVariationEntry create(Object featureType) {
        try {
            Constructor<? extends Feature<FeatureType>> constructor = featureClass.getConstructor(Quantity.class);
            Feature<FeatureType> feature = constructor.newInstance(featureType);
            feature.addEvidence(new Evidence(Evidence.SourceType.PREDICTION, "Variation", "Parameter variation"));
            return new EntityFeatureVariationEntry(entity, feature);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to create Feature "+featureType+".", e);
        }
    }

    @Override
    public String toString() {
        return "Feature:" +
                " E = " + entity.getIdentifier() +
                " F = " + featureClass.getSimpleName();
    }

}
