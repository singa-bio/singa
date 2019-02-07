package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.reactants.EntityExtractionCondition;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.Arrays;

import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.PRODUCT;
import static bio.singa.simulation.model.modules.concentration.reactants.ReactantRole.SUBSTRATE;

/**
 * @author cl
 */
public class SectionDependentReactionBuilder implements ModuleBuilder {

    private SectionDependentReaction module;
    private Simulation simulation;

    public SectionDependentReactionBuilder(Simulation simulation) {
        this.simulation = simulation;
        createModule(simulation);
    }

    @Override
    public SectionDependentReaction getModule() {
        return module;
    }

    @Override
    public SectionDependentReaction createModule(Simulation simulation) {
        module = ModuleFactory.setupModule(SectionDependentReaction.class,
                ModuleFactory.Scope.SEMI_NEIGHBOURHOOD_DEPENDENT,
                ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
        module.setSimulation(simulation);
        return module;
    }

    public SectionDependentReactionBuilder identifier(String identifier) {
        module.setIdentifier(identifier);
        return this;
    }

    public SectionDependentReactionBuilder addSubstrate(ChemicalEntity chemicalEntity) {
        module.addReactant(new Reactant(chemicalEntity, SUBSTRATE));
        return this;
    }

    public SectionDependentReactionBuilder addSubstrate(EntityExtractionCondition... substrateExtractionCondition) {
        module.addSubstrateCondition(Arrays.asList(substrateExtractionCondition));
        return this;
    }

    public SectionDependentReactionBuilder addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology) {
        module.addReactant(new Reactant(chemicalEntity, SUBSTRATE, topology));
        return this;
    }

    public SectionDependentReactionBuilder addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
        module.addReactant(new Reactant(chemicalEntity, SUBSTRATE, stoichiometricNumber));
        return this;
    }

    public SectionDependentReactionBuilder addSubstrate(ChemicalEntity chemicalEntity, CellTopology topology, double stoichiometricNumber) {
        module.addReactant(new Reactant(chemicalEntity, SUBSTRATE, topology, stoichiometricNumber));
        return this;
    }

    public SectionDependentReactionBuilder addSubstrate(ChemicalEntity chemicalEntity, double stoichiometricNumber, double reactionOrder) {
        module.addReactant(new Reactant(chemicalEntity, SUBSTRATE, stoichiometricNumber, reactionOrder));
        return this;
    }

    public SectionDependentReactionBuilder addProduct(ChemicalEntity chemicalEntity, CellTopology topology) {
        module.addReactant(new Reactant(chemicalEntity, PRODUCT, topology));
        return this;
    }

    public SectionDependentReactionBuilder addProduct(ChemicalEntity chemicalEntity) {
        module.addReactant(new Reactant(chemicalEntity, PRODUCT));
        return this;
    }

    public SectionDependentReactionBuilder addProduct(ChemicalEntity chemicalEntity, double stoichiometricNumber) {
        module.addReactant(new Reactant(chemicalEntity, PRODUCT, stoichiometricNumber));
        return this;
    }

    public SectionDependentReactionBuilder forwardsRate(RateConstant rateConstant) {
        module.setFeature(rateConstant);
        return this;
    }

    public SectionDependentReactionBuilder backwardsRate(RateConstant rateConstant) {
        module.setFeature(rateConstant);
        return this;
    }

    @Override
    public SectionDependentReaction build() {
        module.postConstruct();
        simulation.addModule(module);
        return module;
    }

}
