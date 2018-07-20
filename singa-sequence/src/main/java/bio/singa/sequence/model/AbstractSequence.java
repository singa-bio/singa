package bio.singa.sequence.model;

import bio.singa.chemistry.features.ChemistryFeatureContainer;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureContainer;
import bio.singa.features.model.Featureable;
import bio.singa.sequence.model.interfaces.Sequence;
import bio.singa.structure.model.families.StructuralFamily;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class AbstractSequence<FamilyType extends StructuralFamily> implements Sequence<FamilyType>, Featureable {

    protected static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    static {
        // add features
    }

    private List<FamilyType> sequence;

    protected FeatureContainer features;

    public AbstractSequence(List<FamilyType> sequence) {
        this.sequence = sequence;
        features = new ChemistryFeatureContainer();
    }

    @Override
    public List<FamilyType> getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return sequence.stream().map(StructuralFamily::getOneLetterCode).collect(Collectors.joining());
    }

    @Override
    public Collection<Feature<?>> getFeatures() {
        return features.getAllFeatures();
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        if (!features.hasFeature(featureTypeClass)) {
            setFeature(featureTypeClass);
        }
        return features.getFeature(featureTypeClass);
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
