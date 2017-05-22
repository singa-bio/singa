package de.bioforscher.singa.units.features.playground;

import de.bioforscher.singa.units.features.diffusivity.Diffusivity;
import de.bioforscher.singa.units.features.molarmass.MolarMass;

/**
 * @author cl
 */
public class FeaturePlayground {

    public static void main(String[] args) throws ClassNotFoundException {
        SomethingFeatureable featureable = new SomethingFeatureable();

        featureable.setFeature(Diffusivity.class);

        System.out.println(featureable.getFeature(MolarMass.class));
        System.out.println(featureable.getFeature(Diffusivity.class));

    }

}
