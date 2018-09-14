package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableQuantityFeature;
import bio.singa.simulation.model.modules.concentration.*;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.modules.concentration.reactants.CatalyticReactant;
import bio.singa.simulation.model.modules.concentration.reactants.KineticLaw;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import tec.uom.se.quantity.Quantities;

import java.util.*;
import java.util.stream.Collectors;

import static bio.singa.features.parameters.Environment.getConcentrationUnit;

/**
 * @author cl
 */
public class DynamicReaction extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    public static KineticLawStep inSimulation(Simulation simulation) {
        return new DynamicReactionBuilder(simulation);
    }

    /**
     * The stoichiometric reactants.
     */
    private List<StoichiometricReactant> substrates;

    private List<StoichiometricReactant> products;

    /**
     * The catalytic reactants for this reaction.
     */
    private List<CatalyticReactant> catalyticReactants;

    /**
     * The kinetic law for this reaction.
     */
    private KineticLaw kineticLaw;

    public void initialize() {
        // apply
        setApplicationCondition(updatable -> true);
        // function
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(RateConstant.class);
        // reference module in simulation
        addModuleToSimulation();
    }

    public void addReactant(Reactant reactant) {
        if (reactant instanceof StoichiometricReactant) {
            addStochiometricReactant(((StoichiometricReactant) reactant));
        } else {
            catalyticReactants.add((CatalyticReactant) reactant);
        }
    }

    public void addStochiometricReactant(StoichiometricReactant stoichiometricReactant) {
        if (stoichiometricReactant.isSubstrate()) {
            substrates.add(stoichiometricReactant);
        } else {
            products.add(stoichiometricReactant);
        }
    }

    public List<StoichiometricReactant> getSubstrates() {
        return substrates;
    }

    public List<StoichiometricReactant> getProducts() {
        return products;
    }

    /**
     * Returns all substrates of this reaction.
     *
     * @return All substrates of this reaction.
     */
    public List<ChemicalEntity> getSubstrateEntities() {
        return substrates.stream()
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all products of this reaction.
     *
     * @return All products of this reaction.
     */
    public List<ChemicalEntity> getProductEntities() {
        return products.stream()
                .map(StoichiometricReactant::getEntity)
                .collect(Collectors.toList());
    }

    protected Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        double velocity = kineticLaw.calculateVelocity(concentrationContainer, supplier.isStrutCalculation());
        Updatable updatable = supplier.getCurrentUpdatable();
        for (Reactant reactant : kineticLaw.getConcentrationMap().values()) {
            if (reactant instanceof StoichiometricReactant) {
                CellSubsection subsection = updatable.getConcentrationContainer().getSubsection(reactant.getPrefferedTopology());
                if (((StoichiometricReactant) reactant).isSubstrate()) {
                    deltas.put(new ConcentrationDeltaIdentifier(updatable, subsection, reactant.getEntity()),
                            new ConcentrationDelta(this, subsection, reactant.getEntity(), Quantities.getQuantity(-velocity, getConcentrationUnit())));
                } else {
                    deltas.put(new ConcentrationDeltaIdentifier(updatable, subsection, reactant.getEntity()),
                            new ConcentrationDelta(this, subsection, reactant.getEntity(), Quantities.getQuantity(velocity, getConcentrationUnit())));
                }
            }
        }
        return deltas;
    }

    /**
     * Returns the kinetic law.
     *
     * @return The kinetic law.
     */
    public KineticLaw getKineticLaw() {
        return kineticLaw;
    }

    /**
     * Sets the kinetic law.
     *
     * @param kineticLaw The kinetic law.
     */
    public void setKineticLaw(KineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

    /**
     * Returns the catalytic reactants.
     *
     * @return The catalytic reactants.
     */
    public List<CatalyticReactant> getCatalyticReactants() {
        return catalyticReactants;
    }

    /**
     * Sets the catalytic reactants.
     *
     * @param catalyticReactants The catalytic reactants.
     */
    public void setCatalyticReactants(List<CatalyticReactant> catalyticReactants) {
        this.catalyticReactants = catalyticReactants;
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return new HashSet<>();
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new DynamicReactionBuilder(simulation);
    }

    public String getReactionString() {
        String substrates = collectSubstrateString();
        String products = collectProductsString();
        if (substrates.length() > 1 && Character.isWhitespace(substrates.charAt(0))) {
            substrates = substrates.substring(1);
        }
        return substrates + " \u27f6 " + products;
    }

    protected String collectSubstrateString() {
        return substrates.stream()
                .map(substrate -> (substrate.getStoichiometricNumber() > 1 ? substrate.getStoichiometricNumber() : "") + " "
                        + substrate.getEntity().getIdentifier())
                .collect(Collectors.joining(" +"));
    }

    protected String collectProductsString() {
        return products.stream()
                .map(product -> (product.getStoichiometricNumber() > 1 ? product.getStoichiometricNumber() : "") + " "
                        + product.getEntity().getIdentifier())
                .collect(Collectors.joining(" +"));
    }

    public interface KineticLawStep {

        KineticLawStep identifier(String identifier);

        ParameterStep kineticLaw(String expression);

        BuildStep kineticLaw(KineticLaw kineticLaw);

    }

    public interface ParameterStep {

        ParameterStep referenceParameter(ScalableQuantityFeature<?> scalableFeature);

        ParameterStep referenceParameter(String parameterIdentifier, ScalableQuantityFeature<?> scalableFeature);

        ParameterStep referenceParameter(Reactant reactant);

        ParameterStep referenceParameter(String parameterIdentifier, Reactant reactant);

        ParameterStep referenceParameter(String parameterIdentifier, double parameter);

        DynamicReaction build();

    }

    public interface BuildStep {

        DynamicReaction build();

    }

    public static class DynamicReactionBuilder implements KineticLawStep, ParameterStep, BuildStep, ModuleBuilder {

        private DynamicReaction module;

        public DynamicReactionBuilder(Simulation simulation) {
            createModule(simulation);
        }

        @Override
        public DynamicReaction getModule() {
            return module;
        }

        @Override
        public DynamicReaction createModule(Simulation simulation) {
            module = ModuleFactory.setupModule(DynamicReaction.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
            module.products = new ArrayList<>();
            module.substrates = new ArrayList<>();
            module.catalyticReactants = new ArrayList<>();
            module.setSimulation(simulation);
            return module;
        }

        @Override
        public KineticLawStep identifier(String identifier) {
            module.setIdentifier(identifier);
            return this;
        }

        @Override
        public ParameterStep kineticLaw(String expression) {
            module.setKineticLaw(new KineticLaw(expression));
            return this;
        }

        @Override
        public BuildStep kineticLaw(KineticLaw kineticLaw) {
            for (Map.Entry<String, Reactant> reactantEntry : kineticLaw.getConcentrationMap().entrySet()) {
                module.addReactant(reactantEntry.getValue());
                module.addReferencedEntity(reactantEntry.getValue().getEntity());
            }
            for (Map.Entry<String, ScalableQuantityFeature> featureEntry : kineticLaw.getFeatureMap().entrySet()) {
                module.setFeature(featureEntry.getValue());
            }
            return this;
        }

        @Override
        public ParameterStep referenceParameter(ScalableQuantityFeature<?> scalableFeature) {
            module.setFeature(scalableFeature);
            module.getKineticLaw().referenceFeature(scalableFeature);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(String parameterIdentifier, ScalableQuantityFeature<?> scalableFeature) {
            module.setFeature(scalableFeature);
            module.getKineticLaw().referenceFeature(parameterIdentifier, scalableFeature);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(Reactant reactant) {
            module.addReactant(reactant);
            module.addReferencedEntity(reactant.getEntity());
            module.getKineticLaw().referenceReactant(reactant);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(String parameterIdentifier, Reactant reactant) {
            module.addReactant(reactant);
            module.addReferencedEntity(reactant.getEntity());
            module.getKineticLaw().referenceReactant(parameterIdentifier, reactant);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(String parameterIdentifier, double parameter) {
            module.getKineticLaw().referenceConstant(parameterIdentifier, parameter);
            return this;
        }

        @Override
        public DynamicReaction build() {
            module.initialize();
            return module;
        }
    }

}
