package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.quantities.ReactionRate;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.parameters.UnitScaler;
import de.bioforscher.singa.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class NthOrderReaction extends Reaction {

    private final Quantity<ReactionRate> rateConstant;
    private Quantity<ReactionRate> appliedRateConstant;

    public NthOrderReaction(Quantity<ReactionRate> rateConstant) {
        this.rateConstant = rateConstant;
        prepareAppliedRateConstant();
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node, CellSection section) {
        // concentrations of substrates that influence the reaction
        Quantity<MolarConcentration> concentration = determineConcentration(node, section, ReactantRole.DECREASING);
        // acceleration = concentration * applied rate
        return Quantities.getQuantity(
                concentration.getValue().doubleValue() * this.appliedRateConstant.getValue().doubleValue(),
                this.appliedRateConstant.getUnit());
    }

    public void prepareAppliedRateConstant() {
        this.appliedRateConstant = UnitScaler.rescaleReactionRate(this.rateConstant,
                EnvironmentalParameters.getInstance().getTimeStep());
    }

    public Quantity<ReactionRate> getRateConstant() {
        return this.rateConstant;
    }


}
