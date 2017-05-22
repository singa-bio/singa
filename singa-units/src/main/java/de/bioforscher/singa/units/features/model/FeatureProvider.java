package de.bioforscher.singa.units.features.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public abstract class FeatureProvider<FeatureType extends Feature> {

    private Class<? extends Feature> providedFeature;
    private Set<Class<? extends Feature>> requirements;

    protected FeatureProvider() {
        this.requirements = new HashSet<>();
    }

    protected void setProvidedFeature(Class<? extends Feature> providedFeature) {
        this.providedFeature = providedFeature;
    }

    protected <RequirementType extends Feature> void addRequirement(Class<RequirementType> feature) {
        this.requirements.add(feature);
    }

    /**
     * Tries to resolve all required Features. If circular requirements exist this might result in infinite regression.
     *
     * @param featureable The {@link Featureable} entity to be annotated.
     */
    private void resolveRequirements(Featureable featureable) {
        if (this.requirements != null) {
            for (Class<? extends Feature> requirement : this.requirements) {
                if (!featureable.hasFeature(requirement)) {
                    featureable.setFeature(requirement);
                }
            }
        }
    }

    public  <FeatureableType extends Featureable> void assign(FeatureableType featureable) {
        if (!featureable.hasFeature(this.providedFeature)) {
            if (featureable.canBeFeaturedWith(this.providedFeature)) {
                resolveRequirements(featureable);
                featureable.setFeature(provide(featureable));
            } else {
                throw new IllegalArgumentException("The Feature "+this.providedFeature+" is not available for " + featureable.getClass().getSimpleName());
            }
        }
    }

    public abstract <FeatureableType extends Featureable> FeatureType provide(FeatureableType featureable);

}
