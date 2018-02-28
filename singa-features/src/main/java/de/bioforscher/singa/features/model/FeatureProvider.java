package de.bioforscher.singa.features.model;

import de.bioforscher.singa.features.exceptions.FeatureUnassignableException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public abstract class FeatureProvider<FeatureType extends Feature> {

    private final Set<Class<? extends Feature>> requirements;
    private Class<? extends Feature> providedFeature;

    protected FeatureProvider() {
        requirements = new HashSet<>();
    }

    protected void setProvidedFeature(Class<? extends Feature> providedFeature) {
        this.providedFeature = providedFeature;
    }

    protected <RequirementType extends Feature> void addRequirement(Class<RequirementType> feature) {
        requirements.add(feature);
    }

    /**
     * Tries to resolve all required Features. If circular requirements exist this might result in infinite regression.
     *
     * @param featureable The {@link Featureable} entity to be annotated.
     */
    private void resolveRequirements(Featureable featureable) {
        for (Class<? extends Feature> requirement : requirements) {
            // if feature is not present
            if (!featureable.hasFeature(requirement)) {
                // assign it
                featureable.setFeature(requirement);
                // check if feature could be assigned
                if (!featureable.hasFeature(requirement)) {
                    throw new FeatureUnassignableException("The feature " + providedFeature + " could not be assigned, since " + requirement + " could not be resolved.");
                }
            }
        }
    }

    public <FeatureableType extends Featureable> void assign(FeatureableType featureable) {
        if (!featureable.hasFeature(providedFeature)) {
            if (featureable.canBeFeaturedWith(providedFeature)) {
                resolveRequirements(featureable);
                featureable.setFeature(provide(featureable));
            } else {
                throw new FeatureUnassignableException("The feature " + providedFeature + " is not assignable to " + featureable.getClass().getSimpleName());
            }
        }
    }

    public abstract <FeatureableType extends Featureable> FeatureType provide(FeatureableType featureable);

}
