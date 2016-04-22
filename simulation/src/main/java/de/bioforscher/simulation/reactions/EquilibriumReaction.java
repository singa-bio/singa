package de.bioforscher.simulation.reactions;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;
import java.util.List;
import java.util.Map;

/**
 * A reaction type that calculates the next concentration. Based on reversible
 * reactions.
 *
 * @author Christoph Leberecht
 */
public class EquilibriumReaction extends Reaction {

    private Quantity<ReactionRate> rateConstantForwards;
    private Quantity<ReactionRate> rateConstantBackwards;

    public EquilibriumReaction(List<ChemicalEntity> substrates, List<ChemicalEntity> products,
                               Map<ChemicalEntity, Integer> stoichiometricCoefficients, Quantity<ReactionRate> rateConstantForwards,
                               Quantity<ReactionRate> rateConstantBackwards) {
        super(substrates, products, stoichiometricCoefficients);
        this.rateConstantForwards = rateConstantForwards;
        this.rateConstantBackwards = rateConstantBackwards;
    }

    @Override
    public void updateConcentrations(BioNode node) {
        calculateVelocity(node);
        limitReactionRate(node);
        decreaseSubstrates(node);
        increaseProducts(node);
    }

    public Quantity<ReactionRate> getRateConstantForwards() {
        return this.rateConstantForwards;
    }

    public void setRateConstantForwards(Quantity<ReactionRate> rateConstantForwards) {
        this.rateConstantForwards = rateConstantForwards;
    }

    public Quantity<ReactionRate> getRateConstantBackwards() {
        return this.rateConstantBackwards;
    }

    public void setRateConstantBackwards(Quantity<ReactionRate> rateConstantBackwards) {
        this.rateConstantBackwards = rateConstantBackwards;
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
}
