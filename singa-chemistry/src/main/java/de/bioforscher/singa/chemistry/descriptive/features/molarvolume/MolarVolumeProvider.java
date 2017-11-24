package de.bioforscher.singa.chemistry.descriptive.features.molarvolume;

import de.bioforscher.singa.chemistry.descriptive.features.structure3d.Structure3D;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.mathematics.algorithms.geometry.Abacus;
import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.structure.features.molarvolume.MolarVolume;
import de.bioforscher.singa.structure.model.oak.Structures;

import java.util.List;

/**
 * @author cl
 */
public class MolarVolumeProvider extends FeatureProvider<MolarVolume> {



    public MolarVolumeProvider() {
        setProvidedFeature(MolarVolume.class);
        addRequirement(Structure3D.class);
    }

    @Override
    public <FeatureableType extends Featureable> MolarVolume provide(FeatureableType featureable) {
        final Structure3D feature = featureable.getFeature(Structure3D.class);
        List<Sphere> spheres = Structures.convertToSpheres(feature.getFeatureContent());
        // choose which correlation to take
        final double predictedValue = Abacus.predict(spheres);
        return new MolarVolume(predictedValue, new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION, "Ott", "something"));
    }



}
