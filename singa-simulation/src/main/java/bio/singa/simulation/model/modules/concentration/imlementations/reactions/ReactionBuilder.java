package bio.singa.simulation.model.modules.concentration.imlementations.reactions;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.chemistry.features.reactions.*;
import bio.singa.simulation.reactions.ReactionNetworkGenerator;
import bio.singa.simulation.reactions.reactors.ReactionChain;
import bio.singa.features.model.AbstractScalableQuantitativeFeature;
import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.concentrations.ConcentrationCondition;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.DynamicKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.MichaelisMentenKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.kineticlaws.ReversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.RuleBasedReactantBehavior;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.StaticReactantBehavior;
import bio.singa.simulation.model.parameters.Parameter;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole.*;

/**
 * @author cl
 */
public class ReactionBuilder {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ReactionBuilder.class);

    public static StaticReactantStep staticReactants(Simulation simulation) {
        return new StaticBuilder(simulation);
    }

    public static RuleBasedStep ruleBased(Simulation simulation) {
        return new RuleBasedBuilder(simulation);
    }

    public static void generateNetwork() {
        RuleBasedBuilder.networkGenerator.generate();
    }

    public interface KineticStep {

        /**
         * Reversible reactions where the substrates form products, and products can also from substrates.
         * <pre>
         *  A &lt;-&gt; B</pre>
         * The corresponding rate law is as follows:
         * <pre>
         *  v = kfwd * cA - kbwd * cB</pre>
         * where v is the velocity of the reaction, kfwd is any {@link ForwardsRateConstant}, kbwd is any
         * {@link BackwardsRateConstant}, cA is the concentration of the substrate, and cB is the concentration of
         * the product.
         *
         * @return reversible reaction step
         */
        ReversibleReactionStep reversible();

        IrreversibleReactionStep irreversible();

        /**
         * Complex building reactions classically require two substrates that react to build one complex. The reaction
         * rate is described by a association rate (k1 or forwards rate) and a dissociation rate (k-1 or backwards rate).
         *
         * @return complex building reaction step
         */
        ComplexBuildingReactionStep complexBuilding();

        MichaelisMentenReactionStep michaelisMenten();

        ParameterStep kineticLaw(String expression);
    }

    public interface StaticReactantStep extends KineticStep {

        StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity);

        StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology);

        StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber);

        StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber);


        StaticReactantStep addProduct(ChemicalEntity chemicalEntity);

        StaticReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology);

        StaticReactantStep addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber);

        StaticReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber);


        StaticReactantStep addCatalyst(ChemicalEntity chemicalEntity);

        StaticReactantStep addCatalyst(ChemicalEntity chemicalEntity, CellTopology topology);

    }

    public interface RuleBasedStep {

        KineticStep rule(ReactionChain rule);

        void preReaction(ReactionChain chain);

    }

    public interface ReversibleReactionStep {

        ReversibleReactionStep forwardReactionRate(RateConstant rateConstant);

        FinalStep backwardReactionRate(RateConstant rateConstant);

    }

    public interface IrreversibleReactionStep {

        FinalStep rate(RateConstant rateConstant);

    }

    public interface ComplexBuildingReactionStep {

        ComplexBuildingReactionStep associationRate(RateConstant rateConstant);

        FinalStep dissociationRate(RateConstant rateConstant);

    }

    public interface MichaelisMentenReactionStep {

        MichaelisMentenReactionStep michaelisConstant(MichaelisConstant michaelisConstant);

        FinalStep turnover(TurnoverNumber turnoverNumber);

    }

    public interface FinalStep {

        FinalStep identifier(String identifier);

        FinalStep evidence(Evidence evidence);

        FinalStep evidence(Evidence... evidence);

        FinalStep condition(ConcentrationCondition condition);

        Reaction build();

    }

    public interface ParameterStep extends FinalStep {

        ParameterStep referenceParameter(AbstractScalableQuantitativeFeature<?> scalableFeature);

        ParameterStep referenceParameter(String parameterIdentifier, AbstractScalableQuantitativeFeature<?> scalableFeature);

        ParameterStep referenceParameter(Reactant reactant);

        ParameterStep referenceParameter(Parameter<?> reactant);

        ParameterStep referenceParameter(String parameterIdentifier, Reactant reactant);

        ParameterStep referenceParameter(String parameterIdentifier, double parameter);

        ParameterStep referenceParameter(String parameterIdentifier, double parameter, Evidence evidence);

    }

    public static class GeneralReactionBuilder implements ModuleBuilder, ReversibleReactionStep, IrreversibleReactionStep, ComplexBuildingReactionStep, MichaelisMentenReactionStep, ParameterStep {

        protected Simulation simulation;
        protected Reaction reaction;

        private DynamicKineticLaw dynamicKineticLaw;

        public GeneralReactionBuilder(Simulation simulation) {
            this.simulation = simulation;
            createModule(simulation);
        }

        @Override
        public Reaction getModule() {
            return reaction;
        }

        @Override
        public Reaction createModule(Simulation simulation) {
            reaction = ModuleFactory.setupModule(Reaction.class,
                    ModuleFactory.Scope.SEMI_NEIGHBOURHOOD_DEPENDENT,
                    ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
            reaction.setSimulation(simulation);
            return reaction;
        }

        public ReversibleReactionStep reversible() {
            reaction.setKineticLaw(new ReversibleKineticLaw(reaction));
            return this;
        }

        public IrreversibleReactionStep irreversible() {
            reaction.setKineticLaw(new IrreversibleKineticLaw(reaction));
            return this;
        }

        public MichaelisMentenReactionStep michaelisMenten() {
            reaction.setKineticLaw(new MichaelisMentenKineticLaw(reaction));
            if (reaction.getReactantBehavior().getSubstrates().size() > 1) {
                logger.warn("Only one substrate is considered in classical Michaelis-Meten kinetics.");
            }
            if (reaction.getReactantBehavior().getSubstrates().size() > 1) {
                logger.warn("Only one catalyst (enzyme) is considered in classical Michaelis-Meten kinetics.");
            }
            return this;
        }

        @Override
        public ReversibleReactionStep forwardReactionRate(RateConstant rateConstant) {
            reaction.setFeature(rateConstant);
            return this;
        }

        @Override
        public FinalStep backwardReactionRate(RateConstant rateConstant) {
            reaction.setFeature(rateConstant);
            return this;
        }

        @Override
        public FinalStep rate(RateConstant rateConstant) {
            reaction.setFeature(rateConstant);
            return this;
        }

        @Override
        public ComplexBuildingReactionStep associationRate(RateConstant rateConstant) {
            reaction.setFeature(rateConstant);
            return this;
        }

        @Override
        public FinalStep dissociationRate(RateConstant rateConstant) {
            reaction.setFeature(rateConstant);
            return this;
        }

        public MichaelisMentenReactionStep michaelisConstant(MichaelisConstant michaelisConstant) {
            reaction.setFeature(michaelisConstant);
            return this;
        }

        public FinalStep turnover(TurnoverNumber turnoverNumber) {
            reaction.setFeature(turnoverNumber);
            return this;
        }

        @Override
        public FinalStep identifier(String identifier) {
            reaction.setIdentifier(identifier);
            return this;
        }

        @Override
        public FinalStep evidence(Evidence evidence) {
            reaction.addEvidence(evidence);
            return this;
        }

        @Override
        public FinalStep evidence(Evidence... evidence) {
            for (Evidence e : evidence) {
                reaction.addEvidence(e);
            }
            return this;
        }

        public ParameterStep kineticLaw(String expression) {
            dynamicKineticLaw = new DynamicKineticLaw(reaction, expression);
            reaction.setKineticLaw(dynamicKineticLaw);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(AbstractScalableQuantitativeFeature<?> scalableFeature) {
            dynamicKineticLaw.referenceFeature(scalableFeature);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(String parameterIdentifier, AbstractScalableQuantitativeFeature<?> scalableFeature) {
            dynamicKineticLaw.referenceFeature(parameterIdentifier, scalableFeature);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(Parameter<?> reactant) {
            dynamicKineticLaw.referenceParameter(reactant);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(Reactant reactant) {
            reaction.getReactantBehavior().addReactant(reactant);
            dynamicKineticLaw.referenceReactant(reactant);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(String parameterIdentifier, Reactant reactant) {
            reaction.getReactantBehavior().addReactant(reactant);
            dynamicKineticLaw.referenceReactant(parameterIdentifier, reactant);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(String parameterIdentifier, double parameter) {
            dynamicKineticLaw.referenceConstant(parameterIdentifier, parameter);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(String parameterIdentifier, double parameter, Evidence evidence) {
            dynamicKineticLaw.referenceConstant(parameterIdentifier, parameter, evidence);
            return this;
        }

        @Override
        public FinalStep condition(ConcentrationCondition condition) {
            reaction.getConditions().put(condition.getPriority(), condition);
            return this;
        }

        @Override
        public Reaction build() {
            reaction.postConstruct();
            simulation.addModule(reaction);
            return reaction;
        }

    }

    public static class StaticBuilder extends GeneralReactionBuilder implements StaticReactantStep {

        private StaticReactantBehavior staticReactantBehavior;

        public StaticBuilder(Simulation simulation) {
            super(simulation);
            staticReactantBehavior = new StaticReactantBehavior();
            reaction.setReactantBehavior(staticReactantBehavior);
        }

        public ComplexBuildingReactionStep complexBuilding() {
            reaction.setKineticLaw(new ReversibleKineticLaw(reaction));
            return this;
        }

        @Override
        public StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity) {
            staticReactantBehavior.addSubstrate(new Reactant(chemicalEntity, SUBSTRATE));
            return this;
        }

        @Override
        public StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology) {
            staticReactantBehavior.addSubstrate(new Reactant(chemicalEntity, SUBSTRATE, topology));
            return this;
        }

        @Override
        public StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            staticReactantBehavior.addSubstrate(new Reactant(chemicalEntity, SUBSTRATE, stoichiometricNumber));
            return this;
        }

        @Override
        public StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber) {
            staticReactantBehavior.addSubstrate(new Reactant(chemicalEntity, SUBSTRATE, topology, stoichiometricNumber));
            return this;
        }

        @Override
        public StaticReactantStep addProduct(ChemicalEntity chemicalEntity) {
            staticReactantBehavior.addProduct(new Reactant(chemicalEntity, PRODUCT));
            return this;
        }

        @Override
        public StaticReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology) {
            staticReactantBehavior.addProduct(new Reactant(chemicalEntity, PRODUCT, topology));
            return this;
        }

        @Override
        public StaticReactantStep addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            staticReactantBehavior.addProduct(new Reactant(chemicalEntity, PRODUCT, stoichiometricNumber));
            return this;
        }

        @Override
        public StaticReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber) {
            staticReactantBehavior.addProduct(new Reactant(chemicalEntity, PRODUCT, topology, stoichiometricNumber));
            return this;
        }

        @Override
        public StaticReactantStep addCatalyst(ChemicalEntity chemicalEntity) {
            staticReactantBehavior.addCatalyst(new Reactant(chemicalEntity, CATALYTIC));
            return this;
        }

        @Override
        public StaticReactantStep addCatalyst(ChemicalEntity chemicalEntity, CellTopology topology) {
            staticReactantBehavior.addCatalyst(new Reactant(chemicalEntity, CATALYTIC, topology));
            return this;
        }

    }

    public static class RuleBasedBuilder extends GeneralReactionBuilder implements RuleBasedStep, KineticStep {

        private static ReactionNetworkGenerator networkGenerator = new ReactionNetworkGenerator();

        public RuleBasedBuilder(Simulation simulation) {
            super(simulation);
        }

        @Override
        public KineticStep rule(ReactionChain chain) {
            networkGenerator.add(chain);
            reaction.setReactantBehavior(new RuleBasedReactantBehavior(chain));
            if (chain.getIdentifier() != null && !chain.getIdentifier().isEmpty()) {
                reaction.setIdentifier(chain.getIdentifier());
            }
            return this;
        }

        @Override
        public void preReaction(ReactionChain chain) {
            networkGenerator.addPreReaction(chain);
        }

        @Override
        public ComplexBuildingReactionStep complexBuilding() {
            reaction.setKineticLaw(new ReversibleKineticLaw(reaction));
            return this;
        }

        @Override
        public Reaction build() {
            super.build();
            ((RuleBasedReactantBehavior) reaction.getReactantBehavior()).prepareReactionSets();
            return reaction;
        }
    }

}
