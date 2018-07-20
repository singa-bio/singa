package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.features.reactions.RateConstant;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.endocytosis.ActinBoostVelocity;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementDelta;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;

import static de.bioforscher.singa.simulation.features.DefautFeatureSources.EHRLICH2004;
import static de.bioforscher.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.ACTIN_DEPOLYMERIZATION;
import static de.bioforscher.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.UNATTACHED;
import static de.bioforscher.singa.simulation.model.modules.displacement.Vesicle.TargetDirection.MINUS;
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
    private ChemicalEntity decayingEntity;

    public EndocytosisActinBoost() {
        // delta function
        addDeltaFunction(this::calculateDisplacement, vesicle -> vesicle.getAttachmentState() == ACTIN_DEPOLYMERIZATION);
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

    public DisplacementDelta calculateDisplacement(Vesicle vesicle) {
        // calculate speed based on clathrins available
        double numberOfClathrins = MolarConcentration.concentrationToMolecules(vesicle.getConcentrationContainer().get(MEMBRANE, decayingEntity),
                Environment.getSubsectionVolume()).getValue().doubleValue();
        if (numberOfClathrins < 1) {
            vesicle.setAttachmentState(UNATTACHED);
            // TODO alter for vesicles moving from centre to outside
            vesicle.setTargetDirection(MINUS);
        }
        Quantity<Speed> systemSpeed = scaledVelocity.multiply(numberOfClathrins);
        Quantity<Length> distance = Quantities.getQuantity(systemSpeed.getValue().doubleValue(), Environment.getNodeDistanceUnit());
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
