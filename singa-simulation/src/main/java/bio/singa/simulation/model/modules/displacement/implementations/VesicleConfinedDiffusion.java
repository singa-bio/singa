package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.features.AppliedVesicleState;
import bio.singa.simulation.features.ContainmentRegion;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.VesicleState;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.displacement.DisplacementDelta;
import bio.singa.simulation.model.sections.CellRegion;
import tec.uom.se.quantity.Quantities;

/**
 * @author cl
 */
public class VesicleConfinedDiffusion extends DisplacementBasedModule {

    private static final double SQRT2 = Math.sqrt(2.0);

    public VesicleConfinedDiffusion() {
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getState().equals(getConfiningState()));
        // feature
        getRequiredFeatures().add(Diffusivity.class);
        getRequiredFeatures().add(AppliedVesicleState.class);
        getRequiredFeatures().add(ContainmentRegion.class);
    }

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        double scaling = SQRT2 * Environment.convertSystemToSimulationScale(Quantities.getQuantity(Math.sqrt(vesicle.getFeature(Diffusivity.class).getScaledQuantity()), UnitRegistry.getSpaceUnit()));
        Vector2D gaussian = Vectors.generateStandardGaussian2DVector();
        return new DisplacementDelta(this, gaussian.multiply(scaling));
    }

    public VesicleState getConfiningState() {
        return getFeature(AppliedVesicleState.class).getContent();
    }

    public CellRegion getConfinedVolume() {
        return getFeature(ContainmentRegion.class).getContent();
    }

}
