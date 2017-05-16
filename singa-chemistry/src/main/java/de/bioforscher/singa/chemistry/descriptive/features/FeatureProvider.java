package de.bioforscher.singa.chemistry.descriptive.features;

import javax.measure.Quantity;
import java.util.Set;

/**
 * This abstract class is used to assign a Feature to any entity that is able to be assigned with the feature. Any
 * Features that might be required for the determination of this Feature are also assigned if added to the requirements.
 *
 * @author cl
 */
public abstract class FeatureProvider<FeatureType extends Quantity<FeatureType>> {

    /**
     * Contains the entities that might be assigned with this feature.
     */
    private Set<FeatureAvailability> availabilities;

    /**
     * Contains the requirement that are needed to assign this feature.
     */
    private Set<FeatureKind> requirements;

    /**
     * Annotates a {@link Feature} to a {@link Featureable} entity. This method first checks availability and resolves
     * requirements if any are required.
     *
     * @param featureable The {@link Featureable} entity to be annotated.
     * @param <FeaturableType> The type of the {@link Featureable}
     */
    public <FeaturableType extends Featureable> void annotate(FeaturableType featureable) {
        if (isAvailableFor(featureable)) {
            resolveRequirements(featureable);
            featureable.assignFeature(getFeatureFor(featureable));
        } else {
            throw new IllegalArgumentException("This Feature is not available for " + featureable.getClass().getSimpleName());
        }
    }

    /**
     * Returns {@code true} if this FeatureProvider is able to assign this feature.
     * @param featureable The {@link Featureable} entity to be annotated.
     * @param <FeaturableType> The type of the {@link Featureable}
     * @return {@code true} if this FeatureProvider is able to assign this feature.
     */
    private <FeaturableType extends Featureable> boolean isAvailableFor(FeaturableType featureable) {
        for (FeatureAvailability feature : this.availabilities) {
            Class featureClass = feature.getFeatureClass();
            if (featureable.getClass().equals(featureClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tries to resolve all required Features. If circular requirements exist this might result in infinite regression.
     * @param featureable The {@link Featureable} entity to be annotated.
     */
    private void resolveRequirements(Featureable featureable) {
        for (FeatureKind requirement : this.requirements) {
            if (!featureable.hasFeature(requirement)) {
                requirement.getProvider().annotate(featureable);
            }
        }
    }

    /**
     * Returns the entities that might be assigned with this feature.
     * @return The entities that might be assigned with this feature.
     */
    public Set<FeatureAvailability> getAvailabilities() {
        return this.availabilities;
    }

    /**
     * Sets the entities that might be assigned with this feature.
     * @param availabilities The entities that might be assigned with this feature.
     */
    protected void setAvailabilities(Set<FeatureAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    /**
     * Returns the requirement that are needed to assign this feature.
     * @return The requirement that are needed to assign this feature.
     */
    public Set<FeatureKind> getRequirements() {
        return this.requirements;
    }

    /**
     * Sets the requirement that are needed to assign this feature.
     * @param requirements The requirement that are needed to assign this feature.
     */
    protected void setRequirements(Set<FeatureKind> requirements) {
        this.requirements = requirements;
    }

    /**
     * Actually calculates the Feature tat is to be assigned.
     * @param featureable The {@link Featureable} entity to be annotated.
     * @param <FeaturableType> The type of the {@link Featureable}
     * @return The resulting feature.
     */
    protected abstract <FeaturableType extends Featureable> Feature<?> getFeatureFor(FeaturableType featureable);

}
