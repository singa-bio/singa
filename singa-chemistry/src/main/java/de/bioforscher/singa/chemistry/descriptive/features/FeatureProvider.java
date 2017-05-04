package de.bioforscher.singa.chemistry.descriptive.features;

import javax.measure.Quantity;
import java.util.Set;

/**
 * @author cl
 */
public abstract class FeatureProvider<FeatureType extends Quantity<FeatureType>> {

    private Set<FeatureAvailability> availabilities;
    private Set<FeatureKind> requirements;

    public <FeaturableType extends Featureable> void annotate(FeaturableType featureable) {
        if (isAvailableFor(featureable)) {
            resolveRequirements(featureable);
            featureable.assignFeature(getFeatureFor(featureable));
        } else {
            throw new IllegalArgumentException("This Feature is not available for " + featureable.getClass().getSimpleName());
        }
    }

    private <FeaturableType extends Featureable> boolean isAvailableFor(FeaturableType featureable) {
        for (FeatureAvailability feature : this.availabilities) {
            Class featureClass = feature.getFeatureClass();
            if (featureable.getClass().equals(featureClass)) {
                return true;
            }
        }
        return false;
    }

    private void resolveRequirements(Featureable featureable) {
        for (FeatureKind requirement : this.requirements) {
            if (!featureable.hasFeature(requirement)) {
                requirement.getProvider().annotate(featureable);
            }
        }
    }

    public Set<FeatureAvailability> getAvailabilities() {
        return this.availabilities;
    }

    public void setAvailabilities(Set<FeatureAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    public Set<FeatureKind> getRequirements() {
        return this.requirements;
    }

    public void setRequirements(Set<FeatureKind> requirements) {
        this.requirements = requirements;
    }

    protected abstract <FeaturableType extends Featureable> Feature<?> getFeatureFor(FeaturableType featureable);

}
