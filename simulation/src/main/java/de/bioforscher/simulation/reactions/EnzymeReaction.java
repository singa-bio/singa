package de.bioforscher.simulation.reactions;

import de.bioforscher.chemistry.descriptive.Enzyme;
import de.bioforscher.simulation.deprecated.Reaction;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.ReactionRate;

import javax.measure.Quantity;

/**
 * A reaction type that calculates the next concentration using Michaelis-Menten
 * Kinetics.
 *
 * @author Christoph Leberecht
 */
public class EnzymeReaction extends Reaction {

    private Enzyme enzyme;

    protected EnzymeReaction() {

    }

    @Override
    public void updateConcentrations(BioNode node) {
        calculateVelocity(node);
        limitReactionRate(node);
        decreaseSubstrates(node);
        increaseProducts(node);
    }

    public Enzyme getEnzyme() {
        return this.enzyme;
    }

    public void setEnzyme(Enzyme enzyme) {
        this.enzyme = enzyme;
    }

    public void calculateVelocity(BioNode node) {

        Quantity<MolarConcentration> substrateConcentration = node.getConcentration(this.enzyme.getCriticalSubstrate());
        Quantity<MolarConcentration> enzymeConcentration = node.getConcentration(this.enzyme);

        // enzyme constants
        Quantity<ReactionRate> kCat = UnitScaler.rescaleReactionRate(this.enzyme.getTurnoverNumber(),
                EnvironmentalVariables.getInstance().getTimeStep());
        Quantity<MolarConcentration> kM = this.enzyme.getMichaelisConstant();

        // v = kCat * c(E) * c(S) / (km + c(S))
        setCurrentVelocity(kCat.getValue().doubleValue() * enzymeConcentration.getValue().doubleValue()
                * substrateConcentration.getValue().doubleValue()
                / (kM.getValue().doubleValue() + substrateConcentration.getValue().doubleValue()));

    }

    public static class Builder extends Reaction.Builder<EnzymeReaction, Builder> {

        @Override
        protected EnzymeReaction createObject() {
            return new EnzymeReaction();
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

        public Builder enzyme(Enzyme enzyme) {
            this.topLevelObject.setEnzyme(enzyme);
            return this;
        }

    }

}
