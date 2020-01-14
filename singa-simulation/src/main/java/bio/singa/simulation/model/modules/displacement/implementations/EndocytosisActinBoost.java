package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.ActinBoostVelocity;
import bio.singa.simulation.features.BoostMediatingEntity;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.displacement.DisplacementDelta;
import bio.singa.simulation.model.sections.CellTopology;

/**
 * @author cl
 */
public class EndocytosisActinBoost extends DisplacementBasedModule {


    public EndocytosisActinBoost() {
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getState().equals(VesicleStateRegistry.ACTIN_PROPELLED));
        // feature
        getRequiredFeatures().add(ActinBoostVelocity.class);
        getRequiredFeatures().add(BoostMediatingEntity.class);
    }

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        BoostMediatingEntity decayingEntity = getFeature(BoostMediatingEntity.class);
        // calculate speed based on clathrins available
        double pullingEntity = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(CellTopology.MEMBRANE, decayingEntity.getContent())).getValue().doubleValue();
        if (pullingEntity < 1) {
            vesicle.setState(VesicleStateRegistry.UNATTACHED);
        }

        double systemSpeed = getScaledFeature(ActinBoostVelocity.class) * pullingEntity ;
        // determine direction
        Vector2D centre = getSimulation().getMembraneLayer().getMicrotubuleOrganizingCentre().getCircleRepresentation().getMidpoint();
        Vector2D direction = centre.subtract(vesicle.getPosition()).normalize();
        // determine delta
        Vector2D delta = direction.multiply(systemSpeed);
        return new DisplacementDelta(this, delta);
    }

}
