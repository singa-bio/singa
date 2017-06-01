package de.bioforscher.singa.chemistry.descriptive.features.smiles;

import de.bioforscher.singa.units.features.model.AbstractFeature;
import de.bioforscher.singa.units.features.model.FeatureOrigin;

/**
 * @author cl
 */
public class Smiles extends AbstractFeature<String> {

    public Smiles(String smilesString, FeatureOrigin featureOrigin) {
        super(smilesString, featureOrigin);
    }


}
