package de.bioforscher.singa.chemistry.descriptive.features.implementations;

import de.bioforscher.singa.chemistry.descriptive.features.Feature;
import de.bioforscher.singa.chemistry.descriptive.features.FeatureProvider;
import de.bioforscher.singa.chemistry.descriptive.features.Featureable;
import de.bioforscher.singa.units.quantities.MolarMass;

/**
 * @author cl
 */
public class MolarMassFeature extends FeatureProvider<MolarMass> {

    private static MolarMassFeature instance = new MolarMassFeature();

    private MolarMassFeature() {

    }

    public static MolarMassFeature getInstance() {
        if (instance == null) {
            synchronized (MolarMassFeature.class) {
                instance = new MolarMassFeature();
            }
        }
        return instance;
    }

    @Override
    protected Feature<?> getFeatureFor(Featureable featureable) {
        return null;
    }
}
