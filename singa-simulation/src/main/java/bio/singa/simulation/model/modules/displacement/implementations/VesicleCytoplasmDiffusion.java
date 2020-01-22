package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.chemistry.features.diffusivity.PixelDiffusivity;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.displacement.DisplacementDelta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.TAGGED_FOR_EXOCYTOSIS;

/**
 * @author cl
 */
public class VesicleCytoplasmDiffusion extends DisplacementBasedModule {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(VesicleCytoplasmDiffusion.class);

    public VesicleCytoplasmDiffusion() {
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getState().equals(VesicleStateRegistry.UNATTACHED) || vesicle.getState().equals(TAGGED_FOR_EXOCYTOSIS));
        // feature
        getRequiredFeatures().add(PixelDiffusivity.class);
    }

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        double scaling = Math.sqrt(2 * vesicle.getFeature(PixelDiffusivity.class).getScaledQuantity() * UnitRegistry.getTimeScale());
        Vector2D gaussian = Vectors.generateStandardGaussian2DVector();
        return new DisplacementDelta(this, gaussian.multiply(scaling));
    }

    @Override
    public void checkFeatures() {
        logger.debug("The module " + getClass().getSimpleName() + " requires the Feature Diffusivity to be annotated to all vesicles.");
    }
}
