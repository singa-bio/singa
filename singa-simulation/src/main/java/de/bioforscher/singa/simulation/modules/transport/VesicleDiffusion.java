package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import de.bioforscher.singa.simulation.model.layer.SpatialDelta;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.model.VesicleModule;
import tec.uom.se.quantity.Quantities;

/**
 * @author cl
 */
public class VesicleDiffusion extends VesicleModule {

    public VesicleDiffusion(Simulation simulation) {
        super(simulation);
        addDeltaFunction(this::calculateDiffusiveDisplacement, vesicle -> true);
    }

    public SpatialDelta calculateDiffusiveDisplacement(Vesicle vesicle) {
        double scaling = Environment.convertSystemToSimulationScale(Quantities.getQuantity(Math.sqrt(2.0 * vesicle.getDiffusivity().getScaledQuantity().getValue().doubleValue()), Environment.getSystemScale().getUnit()));
        Vector2D gaussian = Vectors.generateStandardGaussian2DVector();
        return new SpatialDelta(this, gaussian.multiply(scaling));
    }


}
