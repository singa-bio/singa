package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.VesicleState;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.displacement.DisplacementDelta;
import tec.uom.se.quantity.Quantities;

/**
 * @author cl
 */
public class VesicleConfinedDiffusion extends DisplacementBasedModule {

    /**
     * As long as a vesicle has the confining state it will not move out of the confined volume;
     */
    private VesicleState confiningState;

    private VolumeLikeAgent confinedVolume;

    private static final double SQRT2 = Math.sqrt(2.0);

    public VesicleConfinedDiffusion(VesicleState confiningState, VolumeLikeAgent confinedVolume) {
        this.confiningState = confiningState;
        this.confinedVolume = confinedVolume;
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getVesicleState().equals(confiningState));
        // feature
        getRequiredFeatures().add(Diffusivity.class);
    }

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        double scaling = SQRT2 * Environment.convertSystemToSimulationScale(Quantities.getQuantity(Math.sqrt(vesicle.getFeature(Diffusivity.class).getScaledQuantity().getValue().doubleValue()), UnitRegistry.getSpaceUnit()));
        Vector2D gaussian = Vectors.generateStandardGaussian2DVector();
        return new DisplacementDelta(this, gaussian.multiply(scaling));
    }

    public VesicleState getConfiningState() {
        return confiningState;
    }

    public VolumeLikeAgent getConfinedVolume() {
        return confinedVolume;
    }

}
