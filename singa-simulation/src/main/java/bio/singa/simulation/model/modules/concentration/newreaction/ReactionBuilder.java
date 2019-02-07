package bio.singa.simulation.model.modules.concentration.newreaction;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.reactions.BackwardsRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.newreaction.kineticlawtypes.IrreversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.newreaction.kineticlawtypes.ReversibleKineticLaw;
import bio.singa.simulation.model.modules.concentration.newreaction.reactanttypes.StaticReactantType;
import bio.singa.simulation.model.modules.concentration.reactants.EntityExtractionCondition;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;

import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.PRODUCT;
import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.SUBSTRATE;

/**
 * @author cl
 */
public class ReactionBuilder {

    public static StaticReactantStep staticReactants(Simulation simulation) {
        return new StaticBuilder(simulation);
    }

    public static DynamicReactantStep dynamicReactants(Simulation simulation) {
        return new DynamicBuilder(simulation);
    }

    public interface DynamicReactantStep {

        DynamicReactantStep addSubstrate(EntityExtractionCondition... conditions);

        DynamicReactantStep addSubstrate(ChemicalEntity chemicalEntity);

        DynamicReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology);

        DynamicReactantStep addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber);

        DynamicReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber);


        DynamicReactantStep addProduct(ChemicalEntity chemicalEntity);

        DynamicReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology);

        DynamicReactantStep addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber);

        DynamicReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber);


        ReversibleReactionStep reversible();

        IrreversibleReactionStep irreversible();

        ComplexBuildingReactionStep complexBuilding();

    }

    public interface StaticReactantStep {

        StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity);

        StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology);

        StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber);

        StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber);


        StaticReactantStep addProduct(ChemicalEntity chemicalEntity);

        StaticReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology);

        StaticReactantStep addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber);

        StaticReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber);


        ReversibleReactionStep reversible();

        IrreversibleReactionStep irreversible();

        ComplexBuildingReactionStep complexBuilding();

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

    public interface FinalStep {

        FinalStep identifier(String identifier);

        Reaction build();

    }

    public static abstract class GeneralReactionBuilder implements ModuleBuilder, ReversibleReactionStep, IrreversibleReactionStep, ComplexBuildingReactionStep, FinalStep {

        protected Simulation simulation;
        protected Reaction reaction;

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
            reaction.getRequiredFeatures().add(ForwardsRateConstant.class);
            reaction.getRequiredFeatures().add(BackwardsRateConstant.class);
            return this;
        }

        public IrreversibleReactionStep irreversible() {
            reaction.setKineticLaw(new IrreversibleKineticLaw(reaction));
            reaction.getRequiredFeatures().add(ForwardsRateConstant.class);
            return this;
        }

        public ComplexBuildingReactionStep complexBuilding() {
            reaction.setKineticLaw(new ReversibleKineticLaw(reaction));
            reaction.getRequiredFeatures().add(ForwardsRateConstant.class);
            reaction.getRequiredFeatures().add(BackwardsRateConstant.class);
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

        @Override
        public FinalStep identifier(String identifier) {
            reaction.setIdentifier(identifier);
            return this;
        }

        @Override
        public Reaction build() {
            reaction.postConstruct();
            simulation.addModule(reaction);
            return reaction;
        }

    }

    public static class DynamicBuilder extends GeneralReactionBuilder implements DynamicReactantStep {

        public DynamicBuilder(Simulation simulation) {
            super(simulation);
        }

        @Override
        public DynamicReactantStep addSubstrate(EntityExtractionCondition... conditions) {
            return null;
        }

        @Override
        public DynamicReactantStep addSubstrate(ChemicalEntity chemicalEntity) {
            return null;
        }

        @Override
        public DynamicReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology) {
            return null;
        }

        @Override
        public DynamicReactantStep addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            return null;
        }

        @Override
        public DynamicReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber) {
            return null;
        }

        @Override
        public DynamicReactantStep addProduct(ChemicalEntity chemicalEntity) {
            return null;
        }

        @Override
        public DynamicReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology) {
            return null;
        }

        @Override
        public DynamicReactantStep addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            return null;
        }

        @Override
        public DynamicReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber) {
            return null;
        }

    }

    public static class StaticBuilder extends GeneralReactionBuilder implements StaticReactantStep {

        private StaticReactantType staticReactantType;

        public StaticBuilder(Simulation simulation) {
            super(simulation);
            staticReactantType = new StaticReactantType();
            reaction.setReactantType(staticReactantType);
        }

        @Override
        public StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity) {
            staticReactantType.addSubstrate(new Reactant(chemicalEntity, SUBSTRATE));
            return this;
        }

        @Override
        public StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology) {
            staticReactantType.addSubstrate(new Reactant(chemicalEntity, SUBSTRATE, topology));
            return this;
        }

        @Override
        public StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            staticReactantType.addSubstrate(new Reactant(chemicalEntity, SUBSTRATE, stoichiometricNumber));
            return this;
        }

        @Override
        public StaticReactantStep addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber) {
            staticReactantType.addSubstrate(new Reactant(chemicalEntity, SUBSTRATE, topology, stoichiometricNumber));
            return this;
        }

        @Override
        public StaticReactantStep addProduct(ChemicalEntity chemicalEntity) {
            staticReactantType.addProduct(new Reactant(chemicalEntity, PRODUCT));
            return this;
        }

        @Override
        public StaticReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology) {
            staticReactantType.addProduct(new Reactant(chemicalEntity, PRODUCT, topology));
            return this;
        }

        @Override
        public StaticReactantStep addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
            staticReactantType.addProduct(new Reactant(chemicalEntity, PRODUCT, stoichiometricNumber));
            return this;
        }

        @Override
        public StaticReactantStep addProduct(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber) {
            staticReactantType.addProduct(new Reactant(chemicalEntity, PRODUCT, topology, stoichiometricNumber));
            return this;
        }

    }

}
