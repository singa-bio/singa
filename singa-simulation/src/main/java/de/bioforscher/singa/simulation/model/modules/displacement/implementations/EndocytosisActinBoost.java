package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.features.reactions.RateConstant;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.endocytosis.ActinBoostVelocity;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.SpatialDelta;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;

import static de.bioforscher.singa.simulation.features.DefautFeatureSources.EHRLICH2004;
import static de.bioforscher.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class EndocytosisActinBoost extends DisplacementBasedModule {

    /**
     * Average vesicle with a radius of 50 nm was coated by 60 clathrins. The depolymerization finished after about
     * 11 seconds.
     *
     * 9.963234242562985E-23 is the concentration of 60 clathrin molecules scaled to 1 mol/um^3
     */
    public static final RateConstant DEFAULT_CLATHRIN_DEPOLYMERIZATION_RATE = RateConstant.create(9.963234242562985E-23/11.0)
            .forward()
            .zeroOrder()
            .concentrationUnit(Environment.getConcentrationUnit())
            .timeUnit(SECOND)
            .origin(EHRLICH2004)
            .build();

    private Quantity<Speed> scaledVelocity;
    private Rectangle simulationRegion;
    private ChemicalEntity decayingEntity;

    public EndocytosisActinBoost() {
        // delta function
        // TODO only if molecules of clathrin is bigger than 0
        addDeltaFunction(this::calculateDisplacement, vesicle -> true);
        // feature
        getRequiredFeatures().add(ActinBoostVelocity.class);
    }

    public void setDecayingEntity(ChemicalEntity decayingEntity) {
        this.decayingEntity = decayingEntity;
    }

    @Override
    public void calculateUpdates() {
        scaledVelocity = getScaledFeature(ActinBoostVelocity.class).multiply(2.0).divide(60.0);
        super.calculateUpdates();
    }

    public void setSimulationRegion(Rectangle simulationRegion) {
        this.simulationRegion = simulationRegion;
    }

    public SpatialDelta calculateDisplacement(Vesicle vesicle) {
        // calculate speed based on clathrins available
        double numberOfClathrins = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(MEMBRANE, decayingEntity), Environment.getSubsectionVolume()).getValue().doubleValue();
        Quantity<Speed> systemSpeed = scaledVelocity.multiply(numberOfClathrins);
        Quantity<Length> distance = Quantities.getQuantity(systemSpeed.getValue().doubleValue(), Environment.getNodeDistanceUnit());
        // determine direction
        Vector2D centre = simulationRegion.getCentre();
        Vector2D direction = centre.subtract(vesicle.getPosition()).normalize();
        // determine delta
        Vector2D delta = direction.multiply(Environment.convertSystemToSimulationScale(distance));
        return new SpatialDelta(this, delta);
    }

    @Override
    public String toString() {
        return "Actin boost after endocytosis";
    }

}
