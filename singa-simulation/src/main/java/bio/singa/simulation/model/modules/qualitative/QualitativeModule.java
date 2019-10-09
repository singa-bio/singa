package bio.singa.simulation.model.modules.qualitative;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.simulation.model.modules.AbstractUpdateModule;
import bio.singa.simulation.model.parameters.FeatureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public abstract class QualitativeModule extends AbstractUpdateModule {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(QualitativeModule.class);

    private FeatureManager featureManager;

    private Set<ChemicalEntity> referencedChemicalEntities;

    public QualitativeModule() {
        referencedChemicalEntities = new HashSet<>();
        featureManager = new FeatureManager();
    }

    @Override
    public void initialize() {

    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return featureManager.getRequiredFeatures();
    }

    @Override
    public double getScaledFeature(Class<? extends ScalableQuantitativeFeature<?>> featureClass) {
        return featureManager.getFeature(featureClass).getScaledQuantity();
    }

    /**
     * Sets a feature.
     * @param feature The feature.
     */
    public void setFeature(Feature<?> feature) {
        featureManager.setFeature(feature);
    }

    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return featureManager.getFeature(featureTypeClass);
    }

    @Override
    public Set<ChemicalEntity> getReferencedEntities() {
        return referencedChemicalEntities;
    }

    @Override
    public void checkFeatures() {
        for (Class<? extends Feature> featureClass : getRequiredFeatures()) {
            if (featureManager.hasFeature(featureClass)) {
                Feature feature = getFeature(featureClass);
                logger.debug("Required feature {} has been set to {}.", feature.getDescriptor(), feature.getContent());
            } else {
                logger.warn("Required feature {} has not been set for module {}.", featureClass.getSimpleName(), getIdentifier());
            }
        }
    }

    public Collection<Feature<?>> getFeatures() {
        return featureManager.getAllFeatures();
    }

}
