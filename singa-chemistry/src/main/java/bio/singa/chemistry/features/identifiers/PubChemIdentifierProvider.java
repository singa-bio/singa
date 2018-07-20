package bio.singa.chemistry.features.identifiers;

import bio.singa.chemistry.features.databases.unichem.UniChemParser;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PubChemIdentifier;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;

/**
 * @author cl
 */
public class PubChemIdentifierProvider extends FeatureProvider<PubChemIdentifier> {

    public PubChemIdentifierProvider() {
        setProvidedFeature(PubChemIdentifier.class);
        addRequirement(InChIKey.class);

    }

    @Override
    public <FeatureableType extends Featureable> PubChemIdentifier provide(FeatureableType featureable) {
        return UniChemParser.fetchPubChemIdentifier(featureable.getFeature(InChIKey.class));
    }

}
