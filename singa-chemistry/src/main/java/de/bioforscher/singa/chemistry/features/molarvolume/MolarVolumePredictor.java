package de.bioforscher.singa.chemistry.features.molarvolume;

import de.bioforscher.singa.chemistry.features.structure3d.Structure3D;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.FeatureProvider;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.quantities.MolarVolume;
import de.bioforscher.singa.mathematics.algorithms.geometry.SphereVolumeEstimaton;
import de.bioforscher.singa.mathematics.geometry.bodies.Sphere;
import de.bioforscher.singa.structure.model.oak.Structures;
import tec.uom.se.quantity.Quantities;

import java.util.List;

import static de.bioforscher.singa.features.quantities.MolarVolume.CUBIC_ANGSTROEM_PER_MOLE;

/**
 * @author cl
 */
public class MolarVolumePredictor extends FeatureProvider<MolarVolume> {

    private static FeatureOrigin OTT1992 = new FeatureOrigin(FeatureOrigin.OriginType.PREDICTION, "Ott1992", "Ott, Rolf, et al. \"A computer method for estimating volumes and surface areas of complex structures consisting of overlapping spheres.\" Mathematical and computer modelling 16.12 (1992): 83-98.");

    public MolarVolumePredictor() {
        setProvidedFeature(MolarVolume.class);
        addRequirement(Structure3D.class);
    }

    @Override
    public <FeatureableType extends Featureable> MolarVolume provide(FeatureableType featureable) {
        final Structure3D feature = featureable.getFeature(Structure3D.class);
        List<Sphere> spheres = Structures.convertToSpheres(feature.getFeatureContent());
        final double predictedValue = SphereVolumeEstimaton.predict(spheres);
        return new MolarVolume(Quantities.getQuantity(predictedValue, CUBIC_ANGSTROEM_PER_MOLE), OTT1992);
    }


}
