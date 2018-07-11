package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.chemistry.features.diffusivity.Diffusivity;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementDelta;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;

import static de.bioforscher.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.MICROTUBULE;

/**
 * @author cl
 */
public class VesicleTransport extends DisplacementBasedModule {

    public VesicleTransport() {
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getAttachmentState() == MICROTUBULE);
        // feature
        getRequiredFeatures().add(Diffusivity.class);
    }

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        // move along with segmental iterator
        // x(t+dt) = x(t) + v * dt * y
        // new position = current position + velocity * time step size * unit direction
        return new DisplacementDelta(this, new Vector2D(0,0));
    }

    @Override
    public String toString() {
        return "Vesicle Transport";
    }


}
