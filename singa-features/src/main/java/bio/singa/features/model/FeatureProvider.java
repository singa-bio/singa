package bio.singa.features.model;

import bio.singa.features.exceptions.FeatureUnassignableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author cl
 */
public abstract class FeatureProvider<FeatureType extends Feature> {

    private static final Logger logger = LoggerFactory.getLogger(FeatureProvider.class);

    private final TreeMap<Integer, Set<Class<? extends Feature>>> strategies;
    private Class<? extends Feature> providedFeature;
    private int retries = 0;

    private int preferredStrategyIndex;

    protected FeatureProvider() {
        strategies = new TreeMap<>();
        preferredStrategyIndex = -1;
    }

    protected void setProvidedFeature(Class<? extends Feature> providedFeature) {
        this.providedFeature = providedFeature;
    }

    protected <RequirementType extends Feature> void addRequirement(Class<RequirementType> feature) {
        addFallbackRequirement(0, feature);
    }

    protected <RequirementType extends Feature> void addFallbackRequirement(Integer priorityGroupIndex, Class<RequirementType> feature) {
        if (!strategies.containsKey(priorityGroupIndex)) {
            strategies.put(priorityGroupIndex, new HashSet<>());
        }
        strategies.get(priorityGroupIndex).add(feature);
    }

    private void resolveRequirements(Featureable featureable) {
        // check if any retrieval method can be used out of the box
        for (Map.Entry<Integer, Set<Class<? extends Feature>>> entry : strategies.entrySet()) {
            Set<Class<? extends Feature>> requirements = entry.getValue();
            if (featureable.meetsAllRequirements(requirements)) {
                preferredStrategyIndex = entry.getKey();
                break;
            }
        }
        // no strategy can be resolved
        if (preferredStrategyIndex == -1) {
            if (retries > 5) {
                return;
            }
            retries++;
            for (Map.Entry<Integer, Set<Class<? extends Feature>>> entry : strategies.entrySet()) {
                for (Class<? extends Feature> featureClass : entry.getValue()) {
                    if (featureable.hasFeature(featureClass)) {
                        continue;
                    }
                    featureable.setFeature(featureClass);
                    if (!featureable.hasFeature(featureClass)) {
                        return;
                    }
                }
                Set<Class<? extends Feature>> requirements = entry.getValue();
                if (featureable.meetsAllRequirements(requirements)) {
                    preferredStrategyIndex = entry.getKey();
                    break;
                }
            }

        }

        boolean resolved = resolveRequirements(featureable, preferredStrategyIndex);

        // if strategies did not work
        if (!resolved) {
            logger.warn("The feature " + providedFeature.getSimpleName() + " could not be assigned, since requirements could not be resolved.");
        }
    }

    /**
     * Tries to resolve all required Features. If circular requirements exist this might result in infinite regression.
     *
     * @param featureable The {@link Featureable} entity to be annotated.
     */
    private boolean resolveRequirements(Featureable featureable, Integer priorityGroupIndex) {
        Set<Class<? extends Feature>> requirements = strategies.get(priorityGroupIndex);
        if (requirements != null) {
            for (Class<? extends Feature> requirement : requirements) {
                logger.debug("Resolving requirement {} for {}.", requirement.getSimpleName(), providedFeature.getSimpleName());
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
        preferredStrategyIndex = priorityGroupIndex;
        return true;
    }

    private <FeatureableType extends Featureable> boolean meetsRequirements(FeatureableType featureable) {
        Set<Class<? extends Feature>> features = strategies.get(preferredStrategyIndex);
        for (Class<? extends Feature> feature : features) {
            if (!featureable.hasFeature(feature)) {
                return false;
            }
        }
        return true;
    }

    public <FeatureableType extends Featureable> void assign(FeatureableType featureable) {
        logger.debug("Assigning {} to {}.", providedFeature.getSimpleName(), featureable);
        if (!featureable.hasFeature(providedFeature)) {
            if (featureable.canBeFeaturedWith(providedFeature)) {
                resolveRequirements(featureable);
                if (retries >= 5 || preferredStrategyIndex == -1) {
                    return;
                }
                retries = 0;
                featureable.setFeature(provide(featureable));
                // reset preferredStrategyIndex
                preferredStrategyIndex = -1;
            } else {
                throw new FeatureUnassignableException("The feature " + providedFeature.getSimpleName() + " is not assignable to " + featureable.getClass().getSimpleName());
            }
        }
    }

    public abstract <FeatureableType extends Featureable> FeatureType provide(FeatureableType featureable);

    public int getPreferredStrategyIndex() {
        return preferredStrategyIndex;
    }
}
