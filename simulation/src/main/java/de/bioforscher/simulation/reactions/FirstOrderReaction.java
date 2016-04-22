package de.bioforscher.simulation.reactions;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;
import java.util.List;
import java.util.Map;

/**
 * A reaction type that calculates the next concentration. Based on first-order
 * reactions.
 *
 * @author Christoph Leberecht
 */
public class FirstOrderReaction extends Reaction {

    private Quantity<ReactionRate> rateConstant;

    protected FirstOrderReaction(List<Species> substrates, List<Species> products,
                                 Map<Species, Integer> stoichiometricCoefficients, Quantity<ReactionRate> rateConstant) {
        super(substrates, products, stoichiometricCoefficients);
        this.rateConstant = rateConstant;
    }

    public Quantity<ReactionRate> getRateConstant() {
        return this.rateConstant;
    }

    public void setRateConstant(Quantity<ReactionRate> reactionRate) {
        this.rateConstant = reactionRate;
    }

    @Override
    public void updateConcentrations(BioNode node) {
        calculateVelocity(node);
        limitReactionRate(node);
        decreaseSubstrates(node);
        increaseProducts(node);
    }

    @Override
    public void calculateVelocity(BioNode node) {
        // get substrate
        // TODO improve this
        final Species rateDeterminingSubstrate = this.getSubstrates().get(0);
        // concentration of substrate
        final double rateDeterminingConcentration = node.getConcentration(rateDeterminingSubstrate).getValue()
                .doubleValue();
        // transform to given unit
        final Quantity<ReactionRate> currentReactionRate = UnitScaler.rescaleReactionRate(this.rateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());
        // v = k * c(A)
        setCurrentVelocity(currentReactionRate.getValue().doubleValue() * rateDeterminingConcentration);
    }

}
