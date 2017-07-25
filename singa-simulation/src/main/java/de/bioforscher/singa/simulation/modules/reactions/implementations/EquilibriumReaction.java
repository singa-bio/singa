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
public class EquilibriumReaction extends Reaction {

    private final Quantity<ReactionRate> forwardsRateConstant;
    private Quantity<ReactionRate> appliedForwardsRateConstant;

    private final Quantity<ReactionRate> backwardsRateConstant;
    private Quantity<ReactionRate> appliedBackwardsRateConstant;

    public EquilibriumReaction(Quantity<ReactionRate> forwardsRateConstant, Quantity<ReactionRate> backwardsRateConstant) {
        this.forwardsRateConstant = forwardsRateConstant;
        this.backwardsRateConstant = backwardsRateConstant;
        prepareAppliedRateConstants();
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node, CellSection section) {
        // concentrations of substrates that influence the reaction
        Quantity<MolarConcentration> substrateConcentration = determineConcentration(node, section, ReactantRole.DECREASING);
        Quantity<MolarConcentration> productConcentration = determineConcentration(node, section, ReactantRole.INCREASING);
        // acceleration = substrate concentration * forwards rate - product concentration * backwards rate
        return Quantities.getQuantity(
                substrateConcentration.getValue().doubleValue() * this.appliedForwardsRateConstant.getValue()
                        .doubleValue() -
                        productConcentration.getValue().doubleValue() * this.appliedBackwardsRateConstant.getValue()
                                .doubleValue(),
                this.appliedForwardsRateConstant.getUnit());
    }

    public void prepareAppliedRateConstants() {
        this.appliedForwardsRateConstant = UnitScaler.rescaleReactionRate(this.forwardsRateConstant,
                EnvironmentalParameters.getInstance().getTimeStep());
        this.appliedBackwardsRateConstant = UnitScaler.rescaleReactionRate(this.backwardsRateConstant,
                EnvironmentalParameters.getInstance().getTimeStep());
    }

    public Quantity<ReactionRate> getBackwardsRateConstant() {
        return this.backwardsRateConstant;
    }

    public Quantity<ReactionRate> getForwardsRateConstant() {
        return this.forwardsRateConstant;
    }
}
