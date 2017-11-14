package de.bioforscher.singa.chemistry.descriptive.features.molarvolume;

import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;

/**
 * @author cl
 */
public class MolarVolumeProvider extends FeatureProvider<MolarVolume> {

    public MolarVolumeProvider() {
        setProvidedFeature(MolarVolume.class);
    }

    @Override
    public <FeatureableType extends Featureable> MolarVolume provide(FeatureableType featureable) {
        return null;
    }

}
