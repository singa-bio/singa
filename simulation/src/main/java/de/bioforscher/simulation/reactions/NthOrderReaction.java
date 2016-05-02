package de.bioforscher.simulation.reactions;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;

public class NthOrderReaction extends Reaction {

    private Quantity<ReactionRate> rateConstant;

    protected NthOrderReaction() {

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

        // product of substrates
        // TODO can this be simplified? is it better to omit the if for the sake
        // of simplicity
        double substrateConcentrationProduct = 1.0;
        for (ChemicalEntity entity : node.getConcentrations().keySet()) {
            if (getSubstrates().contains(entity)) {
                if (getStoichiometricCoefficient(entity) > 0) {
                    substrateConcentrationProduct *= Math.pow(node.getConcentration(entity).getValue().doubleValue(),
                            getStoichiometricCoefficient(entity));
                } else {
                    substrateConcentrationProduct *= node.getConcentration(entity).getValue().doubleValue();
                }
            }
        }
        // transform rate to given unit
        Quantity<ReactionRate> appliedReactionRate = UnitScaler.rescaleReactionRate(this.rateConstant,
                EnvironmentalVariables.getInstance().getTimeStep());
        setCurrentVelocity(substrateConcentrationProduct * appliedReactionRate.getValue().doubleValue());
    }

    public static class Builder extends Reaction.Builder<NthOrderReaction, Builder> {

        @Override
        protected NthOrderReaction createObject() {
            return new NthOrderReaction();
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

        public Builder rateConstant(Quantity<ReactionRate> rateConstant) {
            this.topLevelObject.setRateConstant(rateConstant);
            return this;
        }

    }

}
