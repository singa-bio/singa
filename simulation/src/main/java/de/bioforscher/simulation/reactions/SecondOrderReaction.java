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
 * A reaction type that calculates the next concentration. Based on
 * first2DVector-order reactions.
 *
 * @author Christoph Leberecht
 */
public class SecondOrderReaction extends Reaction {

    private Quantity<ReactionRate> rateConstant;
    private Map<Species, Double> orders;

    public SecondOrderReaction(List<Species> substrates, List<Species> products, Map<Species, Double> orders,
                               Map<Species, Integer> stoichiometricCoefficients, Quantity<ReactionRate> rateConstant) {
        super(substrates, products, stoichiometricCoefficients);
        this.orders = orders;
        initializeRateConstant(rateConstant);
    }

    private void initializeRateConstant(Quantity<ReactionRate> rateConstant) {
        if (getSubstrates().get(0).equals(getSubstrates().get(1))) {
            this.rateConstant = rateConstant.multiply(0.5);
        } else {
            this.rateConstant = rateConstant;
        }
    }

    public Quantity<ReactionRate> getRateConstant() {
        return this.rateConstant;
    }

    public void setRateConstant(Quantity<ReactionRate> rateConstant) {
        this.rateConstant = rateConstant;
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
        Species substrateA = getSubstrates().get(0);
        Species substrateB = getSubstrates().get(1);

        double substrateAConcentration = node.getConcentration(substrateA).getValue().doubleValue();
        double substrateBConcentration = node.getConcentration(substrateB).getValue().doubleValue();

        // transform rate to given unit
        Quantity<ReactionRate> currentReactionRare = UnitScaler.rescaleReactionRate(this.rateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());

        double reactionOrderA = this.orders.get(substrateA);
        double reactionOrderB = this.orders.get(substrateB);

        // calculate velocity v = k * A^a * B^b
        setCurrentVelocity(Math.pow(substrateAConcentration, reactionOrderA)
                * Math.pow(substrateBConcentration, reactionOrderB) * currentReactionRare.getValue().doubleValue());
    }

}
