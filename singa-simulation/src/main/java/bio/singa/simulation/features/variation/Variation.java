package bio.singa.simulation.features.variation;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public abstract class Variation<FeatureType> {

    private Set<FeatureType> variations;

    public Variation() {
        variations = new HashSet<>();
    }

    public Set<FeatureType> getVariations() {
        return variations;
    }

    public void setVariations(Set<FeatureType> variations) {
        this.variations = variations;
    }

    public void addVariation(FeatureType variation) {
        variations.add(variation);
    }

    public abstract Object create(Object featureType);


}
