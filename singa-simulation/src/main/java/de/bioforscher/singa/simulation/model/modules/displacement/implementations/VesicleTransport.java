package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.chemistry.features.diffusivity.Diffusivity;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.SpatialDelta;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;

/**
 * @author cl
 */
public class VesicleTransport extends DisplacementBasedModule {

    // x(t+dt) = x(t) + v * dt * y
    // ne position = current position * velocity * time step size * unit direction

    public VesicleTransport() {
        // delta function
        addDeltaFunction(this::calculateDiffusiveDisplacement, vesicle -> true);
        // feature
        getRequiredFeatures().add(Diffusivity.class);
    }

    public SpatialDelta calculateDiffusiveDisplacement(Vesicle vesicle) {

        return new SpatialDelta(this, new Vector2D(0,0));
    }

    @Override
    public String toString() {
        return "Vesicle Diffusion";
    }


}
