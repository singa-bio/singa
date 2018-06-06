package de.bioforscher.singa.simulation.modules.newmodules.type;

import de.bioforscher.singa.chemistry.descriptive.features.ChemistryFeatureContainer;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.FeatureContainer;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.modules.newmodules.FieldSupplier;

import javax.measure.Quantity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class ModuleFeatureManager implements Featureable {

    /**
     * The features available for automatic annotation and assignment.
     */
    private static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();

    private FieldSupplier supplier;

    /**
     * The features of the reaction.
     */
    private FeatureContainer features;

    public ModuleFeatureManager(FieldSupplier supplier) {
        this.supplier = supplier;
        features = new ChemistryFeatureContainer();
    }

    @Override
    public Collection<Feature<?>> getFeatures() {
        return features.getAllFeatures();
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return features.getFeature(featureTypeClass);
    }

    /**
     * Returns the feature for the entity. The feature is scaled according to the time step size and considering half
     * steps.
     *
     * @param featureClass The feature to get.
     * @param <FeatureContentType> The type of the feature.
     * @return The requested feature for the corresponding entity.
     */
    public <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass) {
        ScalableFeature<FeatureContentType> feature = getFeature(featureClass);
        if (supplier.isStrutCalculation()) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    @Override
    public <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass) {
        features.setFeature(featureTypeClass, this);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        features.setFeature(feature);
    }

    @Override
    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return features.hasFeature(featureTypeClass);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

}
