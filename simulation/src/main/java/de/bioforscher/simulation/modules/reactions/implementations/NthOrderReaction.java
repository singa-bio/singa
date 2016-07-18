package de.bioforscher.simulation.modules.reactions.implementations;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
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
 * Created by Christoph on 08.07.2016.
 */
public class NthOrderReaction extends Reaction {

    private final Quantity<ReactionRate> rateConstant;
    private Quantity<ReactionRate> appliedRateConstant;

    public NthOrderReaction(Quantity<ReactionRate> rateConstant) {
        this.rateConstant = rateConstant;
        prepareAppliedRateConstant();
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        // concentrations of substrates that influence the reaction
        Quantity<MolarConcentration> concentration = determineConcentration(node, ReactantRole.DECREASING);
        // acceleration = concentration * applied rate
        return Quantities.getQuantity(
                concentration.getValue().doubleValue() * this.appliedRateConstant.getValue().doubleValue(),
                this.appliedRateConstant.getUnit());
    }

    @Override
    public Set<ChemicalEntity> collectAllReferencesEntities() {
        return this.getStoichiometricReactants().stream()
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toSet());
    }

    public void prepareAppliedRateConstant() {
        this.appliedRateConstant = UnitScaler.rescaleReactionRate(this.rateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());
    }

    public Quantity<ReactionRate> getRateConstant() {
        return this.rateConstant;
    }


}
