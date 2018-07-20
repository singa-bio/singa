package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.Enzyme;
import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.exceptions.FeatureUnassignableException;
import bio.singa.features.model.Feature;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class MichaelisMentenReaction extends Reaction {

    public static Builder inSimulation(Simulation simulation) {
        return new Builder(simulation);
    }

    private Enzyme enzyme;

    @Override
    public void initialize() {
        // apply
        setApplicationCondition(updatable -> true);
        // function
        SectionDeltaFunction function = new SectionDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(TurnoverNumber.class);
        getRequiredFeatures().add(MichaelisConstant.class);
        // reference module in simulation
        addModuleToSimulation();
    }

    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity<FirstOrderRate> kCat = getScaledFeature(TurnoverNumber.class);
        final Quantity<MolarConcentration> km = getFeature(MichaelisConstant.class).getFeatureContent().to(Environment.getConcentrationUnit());
        // (KCAT * enzyme * substrate) / KM + substrate
        // FIXME currently "only" the first substrate is considered
        // FIXME builder suggest substrates given would matter
        double substrateConcentration = concentrationContainer.get(supplier.getCurrentSubsection(), enzyme.getSubstrates().iterator().next()).getValue().doubleValue();
        double enzymeConcentration = concentrationContainer.get(supplier.getCurrentSubsection(), enzyme).getValue().doubleValue();
        return (kCat.getValue().doubleValue() * enzymeConcentration * substrateConcentration) / (km.getValue().doubleValue() + substrateConcentration);
    }

    @Override
    public void checkFeatures() {
        boolean turnoverNumber = false;
        boolean michaelisConstant = false;
        for (Feature<?> feature : getFeatures()) {
            // any forwards rate constant
            if (feature instanceof TurnoverNumber) {
                turnoverNumber = true;
            }
            // any backwards rate constant
            if (feature instanceof MichaelisConstant) {
                michaelisConstant = true;
            }
        }
        if (!turnoverNumber || !michaelisConstant) {
            throw new FeatureUnassignableException("Required reaction rates unavailable.");
        }
    }


    public static class Builder extends Reaction.Builder<MichaelisMentenReaction, Builder> {

        public Builder(Simulation identifier) {
            super(identifier);
        }

        @Override
        protected MichaelisMentenReaction createObject(Simulation simulation) {
            MichaelisMentenReaction module = ModuleFactory.setupModule(MichaelisMentenReaction.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.SECTION_SPECIFIC);
            module.setSimulation(simulation);
            return module;
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
