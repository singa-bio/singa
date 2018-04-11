package de.bioforscher.singa.features.model;

import de.bioforscher.singa.features.exceptions.FeatureUnassignableException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author cl
 */
public abstract class FeatureProvider<FeatureType extends Feature> {

    private final Set<Class<? extends Feature>> requirements;
    private final TreeMap<Integer, Set<Class<? extends Feature>>> fallbacks;
    private Class<? extends Feature> providedFeature;

    private int resolvedGroupIndex;

    protected FeatureProvider() {
        requirements = new HashSet<>();
        fallbacks = new TreeMap<>();
    }

    protected void setProvidedFeature(Class<? extends Feature> providedFeature) {
        this.providedFeature = providedFeature;
    }

    protected <RequirementType extends Feature> void addRequirement(Class<RequirementType> feature) {
        requirements.add(feature);
    }

    protected <RequirementType extends Feature> void addFallbackRequirement(Integer priorityGroupIndex, Class<RequirementType> feature) {
        if (!fallbacks.containsKey(priorityGroupIndex)) {
            fallbacks.put(priorityGroupIndex, new HashSet<>());
        }
        fallbacks.get(priorityGroupIndex).add(feature);
    }

    private void resolveRequirements(Featureable featureable) {
        // try standard method
        boolean resolved = resolveRequirements(featureable, 0);
        // standard method did not work
        Iterator<Integer> iterator = fallbacks.navigableKeySet().iterator();
        while (!resolved && iterator.hasNext()) {
            Integer next = iterator.next();
            // try fallbacks
            resolved = resolveRequirements(featureable, next);
        }
        // if fallbacks did not work
        if (!resolved) {
            throw new FeatureUnassignableException("The feature " + providedFeature + " could not be assigned, since  requirements could not be resolved.");
        }
    }

    /**
     * Tries to resolve all required Features. If circular requirements exist this might result in infinite regression.
     *
     * @param featureable The {@link Featureable} entity to be annotated.
     */
    private boolean resolveRequirements(Featureable featureable, Integer priorityGroupIndex) {
        Set<Class<? extends Feature>> requirements = null;
        if (priorityGroupIndex == 0) {
            requirements = this.requirements;
        } else {
            fallbacks.get(priorityGroupIndex);
        }
        if (requirements != null) {
            for (Class<? extends Feature> requirement : requirements) {
                // if feature is not present
                if (!featureable.hasFeature(requirement)) {
                    // assign it
                    featureable.setFeature(requirement);
                    // check if feature could be assigned
                    if (!featureable.hasFeature(requirement)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        resolvedGroupIndex = priorityGroupIndex;
        return true;
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

    public int getResolvedGroupIndex() {
        return resolvedGroupIndex;
    }
}
