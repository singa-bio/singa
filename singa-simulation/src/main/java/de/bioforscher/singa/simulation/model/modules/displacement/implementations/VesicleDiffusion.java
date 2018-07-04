package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.chemistry.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.SpatialDelta;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import tec.uom.se.quantity.Quantities;

/**
 * @author cl
 */
public class VesicleDiffusion extends DisplacementBasedModule {

    public VesicleDiffusion() {
        // delta function
        addDeltaFunction(this::calculateDiffusiveDisplacement, vesicle -> true);
        // feature
        getRequiredFeatures().add(Diffusivity.class);
    }

    public SpatialDelta calculateDiffusiveDisplacement(Vesicle vesicle) {
        double scaling = Environment.convertSystemToSimulationScale(Quantities.getQuantity(Math.sqrt(2.0 * vesicle.getFeature(Diffusivity.class).getScaledQuantity().getValue().doubleValue()), Environment.getSystemScale().getUnit()));
        Vector2D gaussian = Vectors.generateStandardGaussian2DVector();
        return new SpatialDelta(this, gaussian.multiply(scaling));
    }

    @Override
    public String toString() {
        return "Vesicle Diffusion";
    }
}
