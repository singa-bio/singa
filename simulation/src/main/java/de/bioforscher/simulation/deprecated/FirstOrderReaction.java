package de.bioforscher.simulation.deprecated;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.units.UnitDictionary.PER_SECOND;

/**
 * A reaction type that calculates the next concentration. Based on first-order
 * reactions.
 *
 * @author Christoph Leberecht
 * @deprecated
 */
public class FirstOrderReaction extends Reaction {

    private Quantity<ReactionRate> rateConstant;

    protected FirstOrderReaction() {

    }

    public Quantity<ReactionRate> getRateConstant() {
        return this.rateConstant;
    }

    public void setRateConstant(Quantity<ReactionRate> reactionRate) {
        this.rateConstant = reactionRate;
    }

    public void setRateConstant(double reactionRate) {
        this.rateConstant = Quantities.getQuantity(reactionRate, PER_SECOND);
    }

    @Override
    public void updateConcentrations(BioNode node) {
        calculateVelocity(node);
        limitReactionRate(node);
        decreaseSubstrates(node);
        increaseProducts(node);
    }

    public void calculateVelocity(BioNode node) {
        // get substrate
        // TODO improve this
        final ChemicalEntity rateDeterminingSubstrate = this.getSubstrates().get(0);
        // concentration of substrate
        final double rateDeterminingConcentration = node.getConcentration(rateDeterminingSubstrate).getValue()
                .doubleValue();
        // transform to given unit
        final Quantity<ReactionRate> currentReactionRate = UnitScaler.rescaleReactionRate(this.rateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());
        // v = k * c(A)
        setCurrentVelocity(currentReactionRate.getValue().doubleValue() * rateDeterminingConcentration);
        System.out.println(getCurrentVelocity());
    }

    public static class Builder extends Reaction.Builder<FirstOrderReaction, Builder> {

        @Override
        protected FirstOrderReaction createObject() {
            return new FirstOrderReaction();
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

    }


}
