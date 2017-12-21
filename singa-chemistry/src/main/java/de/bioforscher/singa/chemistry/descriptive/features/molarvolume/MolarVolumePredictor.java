package de.bioforscher.singa.chemistry.descriptive.features.molarvolume;

import de.bioforscher.singa.chemistry.descriptive.features.structure3d.Structure3D;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.mathematics.algorithms.geometry.OttVolumePrediction;
import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.structure.features.molarvolume.MolarVolume;
import de.bioforscher.singa.structure.model.oak.Structures;

import java.util.List;

/**
 * @author cl
 */
public class MolarVolumePredictor extends FeatureProvider<MolarVolume> {

    public MolarVolumePredictor() {
        setProvidedFeature(MolarVolume.class);
        addRequirement(Structure3D.class);
    }

    @Override
    public <FeatureableType extends Featureable> MolarVolume provide(FeatureableType featureable) {
        final Structure3D feature = featureable.getFeature(Structure3D.class);
        List<Sphere> spheres = Structures.convertToSpheres(feature.getFeatureContent());
        final double predictedValue = OttVolumePrediction.predict(spheres);
        return new MolarVolume(predictedValue, new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION, "Ott1992",
                "Ott, Rolf, et al. \"A computer method for estimating volumes and surface areas of complex structures consisting of overlapping spheres.\" Mathematical and computer modelling 16.12 (1992): 83-98."));
    }



}
