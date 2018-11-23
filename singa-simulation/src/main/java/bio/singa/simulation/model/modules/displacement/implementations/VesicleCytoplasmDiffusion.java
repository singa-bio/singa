package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.features.DefaultFeatureSources;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.displacement.DisplacementDelta;
import tec.uom.se.quantity.Quantities;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;

/**
 * @author cl
 */
public class VesicleCytoplasmDiffusion extends DisplacementBasedModule {

    public static final Diffusivity DEFAULT_VESICLE_DIFFUSIVITY = new Diffusivity(Quantities.getQuantity(6.0E-10, SQUARE_CENTIMETRE_PER_SECOND), DefaultFeatureSources.EHRLICH2004);

    private static final double SQRT2 = Math.sqrt(2.0);

    public VesicleCytoplasmDiffusion() {
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getVesicleState() == VesicleStateRegistry.UNATTACHED);
        // feature
        getRequiredFeatures().add(Diffusivity.class);
    }

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        double scaling = SQRT2 * Environment.convertSystemToSimulationScale(Quantities.getQuantity(Math.sqrt(vesicle.getFeature(Diffusivity.class).getScaledQuantity().getValue().doubleValue()), UnitRegistry.getSpaceUnit()));
        Vector2D gaussian = Vectors.generateStandardGaussian2DVector();
        return new DisplacementDelta(this, gaussian.multiply(scaling));
    }

    @Override
    public String toString() {
        return "Vesicle Diffusion";
    }

}
