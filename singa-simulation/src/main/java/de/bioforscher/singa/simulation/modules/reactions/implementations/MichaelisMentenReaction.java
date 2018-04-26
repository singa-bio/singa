package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.MichaelisConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;

/**
 * @author cl
 */
public class MichaelisMentenReaction extends Reaction {

    public static Builder inSimulation(Simulation simulation) {
        return new Builder(simulation);
    }

    private Enzyme enzyme;

    private MichaelisMentenReaction(Simulation simulation) {
        super(simulation);
        addDeltaFunction(this::calculateDeltas, bioNode -> true);
    }

    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity<Frequency> kCat = getScaledFeature(TurnoverNumber.class);
        final Quantity<MolarConcentration> km = getFeature(MichaelisConstant.class).getFeatureContent();
        // (KCAT * enzyme * substrate) / KM + substrate
        double substrate = concentrationContainer.getAvailableConcentration(getCurrentCellSection(), enzyme.getSubstrates().iterator().next()).getValue().doubleValue();
        double enzyme = concentrationContainer.getAvailableConcentration(getCurrentCellSection(), this.enzyme).getValue().doubleValue();
        return (kCat.getValue().doubleValue() * enzyme * substrate) / (km.getValue().doubleValue() + substrate);
    }

    public static class Builder extends Reaction.Builder<MichaelisMentenReaction, Builder> {

        public Builder(Simulation identifier) {
            super(identifier);
        }

        @Override
        protected MichaelisMentenReaction createObject(Simulation simulation) {
            return new MichaelisMentenReaction(simulation);
        }

        public Builder enzyme(Enzyme enzyme) {
            topLevelObject.enzyme = enzyme;
            topLevelObject.addReferencedEntity(enzyme);
            topLevelObject.setFeature(enzyme.getFeature(TurnoverNumber.class));
            topLevelObject.setFeature(enzyme.getFeature(MichaelisConstant.class));
            return this;
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

    }

}
