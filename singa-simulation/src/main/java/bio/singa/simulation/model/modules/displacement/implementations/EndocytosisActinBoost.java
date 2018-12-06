package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.ActinBoostVelocity;
import bio.singa.simulation.features.DecayingEntity;
import bio.singa.simulation.features.DefaultFeatureSources;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import bio.singa.simulation.model.modules.displacement.DisplacementDelta;
import bio.singa.simulation.model.sections.CellTopology;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;

import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class EndocytosisActinBoost extends DisplacementBasedModule {

    /**
     * Average vesicle with a radius of 50 nm was coated by 60 clathrins. The depolymerization finished after about
     * 11 seconds.
     */
    public static final RateConstant DEFAULT_CLATHRIN_DEPOLYMERIZATION_RATE = RateConstant.create(MolarConcentration.
            moleculesToConcentration(60).to(UnitRegistry.getConcentrationUnit()).getValue().doubleValue()/11.0)
            .forward()
            .zeroOrder()
            .concentrationUnit(UnitRegistry.getConcentrationUnit())
            .timeUnit(SECOND)
            .origin(DefaultFeatureSources.EHRLICH2004)
            .build();

    private Quantity<Speed> scaledVelocity;

    public EndocytosisActinBoost() {
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getVesicleState().equals(VesicleStateRegistry.ACTIN_PROPELLED));
        // feature
        getRequiredFeatures().add(ActinBoostVelocity.class);
        getRequiredFeatures().add(DecayingEntity.class);
    }

    @Override
    public void calculateUpdates() {
        scaledVelocity = getScaledFeature(ActinBoostVelocity.class).multiply(2.0).divide(60.0);
        super.calculateUpdates();
    }

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        DecayingEntity decayingEntity = getFeature(DecayingEntity.class);
        // calculate speed based on clathrins available
        double pullingEntity = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(CellTopology.MEMBRANE, decayingEntity.getFeatureContent())).getValue().doubleValue();
        System.out.println(pullingEntity);
        if (pullingEntity < 1) {
            vesicle.setVesicleState(VesicleStateRegistry.UNATTACHED);
        }
        Quantity<Speed> systemSpeed = scaledVelocity.multiply(pullingEntity);
        Quantity<Length> distance = Quantities.getQuantity(systemSpeed.getValue().doubleValue(), UnitRegistry.getSpaceUnit());
        // determine direction
        Vector2D centre = simulation.getSimulationRegion().getCentre();
        Vector2D direction = centre.subtract(vesicle.getCurrentPosition()).normalize();
        // determine delta
        Vector2D delta = direction.multiply(Environment.convertSystemToSimulationScale(distance));
        return new DisplacementDelta(this, delta);
    }

    @Override
    public String toString() {
        return "Actin boost after endocytosis";
    }

}
