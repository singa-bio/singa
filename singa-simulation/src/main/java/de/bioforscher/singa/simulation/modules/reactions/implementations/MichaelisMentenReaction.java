package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.Enzyme;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.MichaelisConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class MichaelisMentenReaction extends Reaction {

    public static Builder inSimulation(Simulation simulation) {
        return new Builder(simulation);
    }

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();
    static {
        requiredFeatures.add(TurnoverNumber.class);
        requiredFeatures.add(MichaelisConstant.class);
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
        double substrateConcentration = concentrationContainer.get(getCurrentCellSection(), enzyme.getSubstrates().iterator().next()).getValue().doubleValue();
        double enzymeConcentration = concentrationContainer.get(getCurrentCellSection(), enzyme).getValue().doubleValue();
        return (kCat.getValue().doubleValue() * enzymeConcentration * substrateConcentration) / (km.getValue().doubleValue() + substrateConcentration);
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
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
