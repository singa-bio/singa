package bio.singa.chemistry.features.identifiers;

import bio.singa.chemistry.features.databases.pubchem.PubChemDatabase;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;

/**
 * @author cl
 */
public class PubChemToInChIKey extends FeatureProvider<InChIKey> {

    public PubChemToInChIKey() {
        setProvidedFeature(InChIKey.class);
        addRequirement(PubChemIdentifier.class);
    }

    @Override
    public <FeatureableType extends Featureable> InChIKey provide(FeatureableType featureable) {
        PubChemIdentifier pubChemIdentifier = featureable.getFeature(PubChemIdentifier.class);
        return PubChemDatabase.fetchInchiKey(pubChemIdentifier);
    }

}
