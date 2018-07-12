package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.chemistry.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementDelta;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import tec.uom.se.quantity.Quantities;

import static de.bioforscher.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.simulation.features.DefautFeatureSources.EHRLICH2004;

/**
 * @author cl
 */
public class VesicleDiffusion extends DisplacementBasedModule {

    private static final Diffusivity DEFAULT_VESICLE_DIFFUSIVITY = new Diffusivity(Quantities.getQuantity(6.0E-10, SQUARE_CENTIMETRE_PER_SECOND), EHRLICH2004);

    private boolean useEstimation = true;
    private Diffusivity diffusivity;

    public VesicleDiffusion() {
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getAttachmentState() == Vesicle.AttachmentState.UNATTACHED);
        // feature
        getRequiredFeatures().add(Diffusivity.class);
    }

    @Override
    public void calculateUpdates() {
        if (!useEstimation) {
            diffusivity = getFeature(Diffusivity.class);
        }
        super.calculateUpdates();
    }

    public void useEinsteinStrokesDiffusivity() {
        useEstimation = true;
    }

    public void useLiteratureDiffusivity() {
        useEstimation = false;
        setFeature(DEFAULT_VESICLE_DIFFUSIVITY);
    }

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        double scaling;
        if (useEstimation) {
            scaling = Environment.convertSystemToSimulationScale(Quantities.getQuantity(Math.sqrt(2.0 * vesicle.getFeature(Diffusivity.class).getScaledQuantity().getValue().doubleValue()), Environment.getSystemScale().getUnit()));
        } else {
//            scaling = Environment.convertSystemToSimulationScale(Quantities.getQuantity(Math.sqrt(2.0 * diffusivity.getScaledQuantity().getValue().doubleValue()), Environment.getSystemScale().getUnit()));
            scaling = Environment.convertSystemToSimulationScale(Quantities.getQuantity(diffusivity.getScaledQuantity().getValue().doubleValue(), Environment.getSystemScale().getUnit()));
        }
        Vector2D gaussian = Vectors.generateStandardGaussian2DVector();
        return new DisplacementDelta(this, gaussian.multiply(scaling));
    }

    @Override
    public String toString() {
        return "Vesicle Diffusion";
    }
}
