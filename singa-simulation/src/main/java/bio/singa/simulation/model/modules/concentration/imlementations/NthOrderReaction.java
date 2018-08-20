package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.chemistry.features.reactions.ZeroOrderRateConstant;
import bio.singa.features.exceptions.FeatureUnassignableException;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import bio.singa.simulation.model.modules.concentration.reactants.ReactantRole;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;

import javax.measure.Quantity;
import java.util.List;

/**
 *
 *
 * @author cl
 */
public class NthOrderReaction extends Reaction {

    public static NthOrderReactionBuilder inSimulation(Simulation simulation) {
        return new NthOrderReactionBuilder(simulation);
    }

    private RateConstant rateConstant;

    @Override
    public void initialize() {
        // apply
        setApplicationCondition(this::substratesAvailable);
        // function
        SectionDeltaFunction function = new SectionDeltaFunction(this::calculateDeltas, this::containsSubstrate);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(RateConstant.class);
        // reference module in simulation
        addModuleToSimulation();
    }

    private boolean containsSubstrate(ConcentrationContainer concentrationContainer) {
        CellSubsection currentSubsection = supplier.getCurrentSubsection();
        List<ChemicalEntity> substrates = getSubstrateEntities();
        for (ChemicalEntity substrate : substrates) {
            if (concentrationContainer.get(currentSubsection, substrate).getValue().doubleValue() == 0.0) {
                return false;
            }
        }
        return true;
    }


    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        Quantity scaledReactionRate = getScaledReactionRate();
        if (rateConstant instanceof ZeroOrderRateConstant) {
            return scaledReactionRate.getValue().doubleValue();
        }
        // concentrations of substrates that influence the reaction
        double concentration = determineEffectiveConcentration(concentrationContainer, ReactantRole.DECREASING);
        // calculate acceleration
        return concentration * getScaledReactionRate().getValue().doubleValue();
    }

    @Override
    public void checkFeatures() {
        for (Feature<?> feature : getFeatures()) {
            // any forwards rate constant
            if (feature instanceof RateConstant) {
                return;
            }
        }
        throw new FeatureUnassignableException("Required reaction rate unavailable.");
    }

    private Quantity getScaledReactionRate() {
        if (rateConstant == null) {
            for (Feature<?> feature : getFeatures()) {
                // any forwards rate constant
                if (feature instanceof RateConstant) {
                    rateConstant = (RateConstant) feature;
                    break;
                }
            }
        }
        if (supplier.isStrutCalculation()) {
            return rateConstant.getHalfScaledQuantity();
        }
        return rateConstant.getScaledQuantity();
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new NthOrderReactionBuilder(simulation);
    }

    public static class NthOrderReactionBuilder extends Reaction.Builder<NthOrderReaction, NthOrderReactionBuilder> {

        public NthOrderReactionBuilder(Simulation simulation) {
            super(simulation);
        }

        @Override
        protected NthOrderReaction createObject(Simulation simulation) {
            NthOrderReaction module = ModuleFactory.setupModule(NthOrderReaction.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.SECTION_SPECIFIC);
            module.setSimulation(simulation);
            return module;
        }

        public NthOrderReactionBuilder rateConstant(RateConstant rateConstant) {
            topLevelObject.setFeature(rateConstant);
            return this;
        }

        @Override
        protected NthOrderReactionBuilder getBuilder() {
            return this;
        }

    }

}
