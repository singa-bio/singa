package de.bioforscher.singa.chemistry.descriptive.features.smiles;

import de.bioforscher.singa.chemistry.descriptive.features.FeatureRegistry;
import de.bioforscher.singa.features.model.AbstractFeature;
import de.bioforscher.singa.features.model.FeatureOrigin;

/**
 * @author cl
 */
public class Smiles extends AbstractFeature<String> {

    public static void register() {
        FeatureRegistry.addProviderForFeature(Smiles.class, SmilesProvider.class);
    }

    public Smiles(String smilesString, FeatureOrigin featureOrigin) {
        super(smilesString, featureOrigin);
    }

}
