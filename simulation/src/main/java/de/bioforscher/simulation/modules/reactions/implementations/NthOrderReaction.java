package de.bioforscher.simulation.modules.reactions.implementations;

import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.reactions.model.Reaction;
import de.bioforscher.simulation.modules.reactions.model.StoichiometricReactant;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

/**
 * Created by Christoph on 08.07.2016.
 */
public class NthOrderReaction extends Reaction {

    private final Quantity<ReactionRate> rateConstant;
    private Quantity<ReactionRate> appliedRateConstant;
    private boolean elementary;

    public NthOrderReaction(Quantity<ReactionRate> rateConstant) {
        this.rateConstant = rateConstant;
        prepareAppliedRateConstant();
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        double product = 1.0;
        for (StoichiometricReactant reactant : getStoichiometricReactants()) {
            if (reactant.isSubstrate()) {
                if (isElementary()) {
                    // elementary reaction orders are their stoichiometric numbers
                    product *= Math.pow(node.getConcentration(reactant.getEntity()).getValue().doubleValue(),
                            reactant.getStoichiometricNumber());
                } else {
                    product *= Math.pow(node.getConcentration(reactant.getEntity()).getValue().doubleValue(),
                            reactant.getReactionOrder());
                }
            }
        }
        return Quantities.getQuantity(product * this.appliedRateConstant.getValue().doubleValue(),
                this.appliedRateConstant.getUnit());
    }

    public void prepareAppliedRateConstant() {
        this.appliedRateConstant = UnitScaler.rescaleReactionRate(this.rateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());
    }

    public Quantity<ReactionRate> getRateConstant() {
        return this.rateConstant;
    }

    public boolean isElementary() {
        return this.elementary;
    }

    public void setElementary(boolean elementary) {
        this.elementary = elementary;
    }
}
