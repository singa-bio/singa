package de.bioforscher.simulation.deprecated;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

import static de.bioforscher.units.UnitDictionary.PER_SECOND;

/**
 * A reaction type that calculates the next concentration. Based on
 * first2DVector-order reactions.
 *
 * @author Christoph Leberecht
 * @deprecated
 */
public class SecondOrderReaction extends Reaction {

    private Quantity<ReactionRate> rateConstant;
    private Map<ChemicalEntity, Double> orders;

    protected SecondOrderReaction() {
        this.orders = new HashMap<>();
    }

    private void initializeRateConstant(Quantity<ReactionRate> rateConstant) {
        if (getSubstrates().size() > 1) {
            if (getSubstrates().get(0).equals(getSubstrates().get(1))) {
                this.rateConstant = rateConstant.multiply(0.5);
            } else {
                this.rateConstant = rateConstant;
            }
        }
    }

    public Quantity<ReactionRate> getRateConstant() {
        return this.rateConstant;
    }

    public void setRateConstant(Quantity<ReactionRate> rateConstant) {
        this.rateConstant = rateConstant;
    }

    public void setRateConstant(double rateConstant) {
        this.rateConstant = Quantities.getQuantity(rateConstant, PER_SECOND);
    }

    public void addOrder(ChemicalEntity chemicalEntity, double order) {
        this.orders.put(chemicalEntity, order);
    }

    @Override
    public void updateConcentrations(BioNode node) {
        calculateVelocity(node);
        System.out.println(getCurrentVelocity());
        limitReactionRate(node);
        decreaseSubstrates(node);
        increaseProducts(node);
    }

    public void calculateVelocity(BioNode node) {
        ChemicalEntity substrateA = getSubstrates().get(0);
        ChemicalEntity substrateB = getSubstrates().get(1);

        double substrateAConcentration = node.getConcentration(substrateA).getValue().doubleValue();
        double substrateBConcentration = node.getConcentration(substrateB).getValue().doubleValue();

        // transform rate to given unit
        Quantity<ReactionRate> currentReactionRate = UnitScaler.rescaleReactionRate(this.rateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());

        double reactionOrderA = this.orders.get(substrateA);
        double reactionOrderB = this.orders.get(substrateB);

        // calculate velocity v = k * A^a * B^b
        setCurrentVelocity(Math.pow(substrateAConcentration, reactionOrderA)
                * Math.pow(substrateBConcentration, reactionOrderB) * currentReactionRate.getValue().doubleValue());
    }


    public static class Builder extends Reaction.Builder<SecondOrderReaction, Builder> {

        @Override
        protected SecondOrderReaction createObject() {
            return new SecondOrderReaction();
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

        public Builder rateConstant(Quantity<ReactionRate> rateConstant) {
            this.topLevelObject.setRateConstant(rateConstant);
            return this;
        }

        public Builder rateConstant(double rateConstant) {
            this.topLevelObject.setRateConstant(rateConstant);
            return this;
        }

        public Builder addOrder(ChemicalEntity chemicalEntity, double order) {
            this.topLevelObject.addOrder(chemicalEntity, order);
            return this;
        }

        @Override
        public SecondOrderReaction build() {
            this.topLevelObject.initializeRateConstant(this.topLevelObject.rateConstant);
            return this.topLevelObject;
        }
    }

}
