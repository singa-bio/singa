package de.bioforscher.simulation.modules.reactions.implementations;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.graphs.BioNode;
import de.bioforscher.simulation.modules.reactions.model.ReactantRole;
import de.bioforscher.simulation.modules.reactions.model.Reaction;
import de.bioforscher.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 11.07.2016.
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
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        // concentrations of substrates that influence the reaction
        Quantity<MolarConcentration> substrateConcentration = determineConcentration(node, ReactantRole.DECREASING);
        Quantity<MolarConcentration> productConcentration = determineConcentration(node, ReactantRole.INCREASING);
        // acceleration = substrate concentration * forwards rate - product concentration * backwards rate
        return Quantities.getQuantity(
                substrateConcentration.getValue().doubleValue() * this.appliedForwardsRateConstant.getValue()
                        .doubleValue() -
                        productConcentration.getValue().doubleValue() * this.appliedBackwardsRateConstant.getValue()
                                .doubleValue(),
                this.appliedForwardsRateConstant.getUnit());
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.getStoichiometricReactants().stream()
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toSet());
    }

    public void prepareAppliedRateConstants() {
        this.appliedForwardsRateConstant = UnitScaler.rescaleReactionRate(this.forwardsRateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());
        this.appliedBackwardsRateConstant = UnitScaler.rescaleReactionRate(this.backwardsRateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());
    }

    public Quantity<ReactionRate> getBackwardsRateConstant() {
        return this.backwardsRateConstant;
    }

    public Quantity<ReactionRate> getForwardsRateConstant() {
        return this.forwardsRateConstant;
    }
}
