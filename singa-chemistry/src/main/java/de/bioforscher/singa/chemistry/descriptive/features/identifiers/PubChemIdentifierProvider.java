package de.bioforscher.singa.chemistry.descriptive.features.identifiers;

import de.bioforscher.singa.chemistry.descriptive.features.databases.unichem.UniChemParser;
import de.bioforscher.singa.features.identifiers.InChIKey;
import de.bioforscher.singa.features.identifiers.PubChemIdentifier;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;

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
