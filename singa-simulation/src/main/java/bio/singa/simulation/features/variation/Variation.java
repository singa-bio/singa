package bio.singa.simulation.features.variation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public abstract class Variation<FeatureType> {

    private List<FeatureType> variations;

    public Variation() {
        variations = new ArrayList<>();
    }

    public List<FeatureType> getVariations() {
        return variations;
    }

    public void setVariations(List<FeatureType> variations) {
        this.variations = variations;
    }

    public void addVariation(FeatureType variation) {
        variations.add(variation);
    }

    public abstract Object create(Object featureType);

}
