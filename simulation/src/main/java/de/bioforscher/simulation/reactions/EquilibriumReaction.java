package de.bioforscher.simulation.reactions;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.units.UnitDictionary.PER_SECOND;

/**
 * A reaction type that calculates the next concentration. Based on reversible
 * reactions.
 *
 * @author Christoph Leberecht
 */
public class EquilibriumReaction extends Reaction {

    private Quantity<ReactionRate> rateConstantForwards;
    private Quantity<ReactionRate> rateConstantBackwards;

    protected EquilibriumReaction() {

    }

    public Quantity<ReactionRate> getRateConstantForwards() {
        return this.rateConstantForwards;
    }

    public void setRateConstantForwards(Quantity<ReactionRate> rateConstantForwards) {
        this.rateConstantForwards = rateConstantForwards;
    }

    public void setRateConstantForwards(double rateConstantForwards) {
        this.rateConstantForwards = Quantities.getQuantity(rateConstantForwards, PER_SECOND);
    }

    public Quantity<ReactionRate> getRateConstantBackwards() {
        return this.rateConstantBackwards;
    }

    public void setRateConstantBackwards(Quantity<ReactionRate> rateConstantBackwards) {
        this.rateConstantBackwards = rateConstantBackwards;
    }

    public void setRateConstantBackwards(double rateConstantBackwards) {
        this.rateConstantBackwards = Quantities.getQuantity(rateConstantBackwards, PER_SECOND);
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

        // product of substrates
        double substrateConcentrationProduct = 1.0;
        for (ChemicalEntity substrate : node.getConcentrations().keySet()) {
            if (getSubstrates().contains(substrate)) {
                if (getStoichiometricCoefficient(substrate) > 0) {
                    substrateConcentrationProduct *= Math.pow(node.getConcentration(substrate).getValue().doubleValue(),
                            getStoichiometricCoefficient(substrate));
                } else {
                    substrateConcentrationProduct *= node.getConcentration(substrate).getValue().doubleValue();
                }
            }
        }

        // product of products
        double productConcentrationProduct = 1.0;
        for (ChemicalEntity product : node.getConcentrations().keySet()) {
            if (getProducts().contains(product)) {
                if (getStoichiometricCoefficient(product) > 0) {
                    productConcentrationProduct = Math.pow(node.getConcentration(product).getValue().doubleValue(),
                            getStoichiometricCoefficient(product));
                } else {
                    productConcentrationProduct *= node.getConcentration(product).getValue().doubleValue();
                }
            }
        }

        // scale forward rate
        Quantity<ReactionRate> kforwards = UnitScaler.rescaleReactionRate(this.rateConstantForwards,
                EnvironmentalVariables.getInstance().getTimeStep());
        // scale backwards rate
        Quantity<ReactionRate> kBackwards = UnitScaler.rescaleReactionRate(this.rateConstantBackwards,
                EnvironmentalVariables.getInstance().getTimeStep());
        // v = c(S) * kf - c(P) * kb
        setCurrentVelocity(substrateConcentrationProduct * kforwards.getValue().doubleValue()
                - productConcentrationProduct * kBackwards.getValue().doubleValue());

    }

    public static class Builder extends Reaction.Builder<EquilibriumReaction, Builder> {

        @Override
        protected EquilibriumReaction createObject() {
            return new EquilibriumReaction();
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

        public Builder rateConstantForwards(Quantity<ReactionRate> rateConstant) {
            this.topLevelObject.setRateConstantForwards(rateConstant);
            return this;
        }

        public Builder rateConstantForwards(double rateConstant) {
            this.topLevelObject.setRateConstantForwards(rateConstant);
            return this;
        }

        public Builder rateConstantBackwards(Quantity<ReactionRate> rateConstant) {
            this.topLevelObject.setRateConstantBackwards(rateConstant);
            return this;
        }

        public Builder rateConstantBackwards(double rateConstant) {
            this.topLevelObject.setRateConstantBackwards(rateConstant);
            return this;
        }

    }
}
