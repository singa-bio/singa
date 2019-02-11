package bio.singa.features.model;

import bio.singa.features.identifiers.model.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author cl
 */
public abstract class FeatureContainer {

    private final HashMap<Class<? extends Feature>, Feature<?>> content;

    public FeatureContainer() {
        content = new HashMap<>();
    }

    public <FeatureType extends Feature<?>> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return featureTypeClass.cast(content.get(featureTypeClass));
    }

    public abstract <FeatureableType extends Featureable, FeatureType extends Feature<?>> void setFeature(Class<FeatureType> featureTypeClass, FeatureableType featureable);

    public <FeatureType extends Feature<?>> void setFeature(FeatureType feature) {
        content.put(feature.getClass(), feature);
    }

    public <FeatureType extends Feature<?>> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return content.containsKey(featureTypeClass);
    }

    public Collection<Feature<?>> getAllFeatures() {
        return content.values();
    }

    public List<Identifier> getAdditionalIdentifiers() {
        List<Identifier> identifiers = new ArrayList<>();
        for (Feature<?> feature : getAllFeatures()) {
            if (feature instanceof Identifier) {
                identifiers.add((Identifier) feature);
            }
        }
        return identifiers;
    }

}
