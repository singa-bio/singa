package bio.singa.structure.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;
import bio.singa.features.quantities.MolarVolume;
import bio.singa.mathematics.algorithms.geometry.SphereVolumeEstimaton;
import bio.singa.mathematics.geometry.bodies.Sphere;
import bio.singa.structure.model.oak.Structures;
import tech.units.indriya.quantity.Quantities;

import java.util.List;

import static bio.singa.features.quantities.MolarVolume.CUBIC_ANGSTROEM_PER_MOLE;

/**
 * @author cl
 */
public class MolarVolumePredictor extends FeatureProvider<MolarVolume> {

    public static Evidence OTT1992 = new Evidence(Evidence.SourceType.PREDICTION, "Ott 1992", "Ott, Rolf, et al. \"A computer method for estimating volumes and surface areas of complex structures consisting of overlapping spheres.\" Mathematical and computer modelling 16.12 (1992): 83-98.");

    public MolarVolumePredictor() {
        setProvidedFeature(MolarVolume.class);
        addRequirement(Structure3D.class);
    }

    @Override
    public <FeatureableType extends Featureable> MolarVolume provide(FeatureableType featureable) {
        final Structure3D feature = featureable.getFeature(Structure3D.class);
        List<Sphere> spheres = Structures.convertToSpheres(feature.getContent());
        final double predictedValue = SphereVolumeEstimaton.predict(spheres);
        return new MolarVolume(Quantities.getQuantity(predictedValue, CUBIC_ANGSTROEM_PER_MOLE), OTT1992);
    }


}
