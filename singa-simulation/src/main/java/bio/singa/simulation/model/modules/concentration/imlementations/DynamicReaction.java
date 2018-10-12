package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.model.ScalableQuantityFeature;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.concentration.*;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.modules.concentration.reactants.KineticLaw;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.*;

/**
 * @author cl
 */
public class DynamicReaction extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    public static KineticLawStep inSimulation(Simulation simulation) {
        return new DynamicReactionBuilder(simulation);
    }

    /**
     * The substrates.
     */
    private List<Reactant> substrates;

    /**
     * The products.
     */
    private List<Reactant> products;

    /**
     * The catalysts
     */
    private List<Reactant> catalysts;

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
        switch (reactant.getRole()) {
            case PRODUCT:
                products.add(reactant);
                break;
            case SUBSTRATE:
                substrates.add(reactant);
                break;
            case CATALYTIC:
                catalysts.add(reactant);
                break;
        }
    }

    public List<Reactant> getSubstrates() {
        return substrates;
    }

    public List<Reactant> getProducts() {
        return products;
    }

    public List<Reactant> getCatalysts() {
        return catalysts;
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        double velocity = kineticLaw.calculateVelocity(concentrationContainer, supplier.isStrutCalculation());
        Updatable updatable = supplier.getCurrentUpdatable();
        for (Reactant reactant : kineticLaw.getConcentrationMap().values()) {
            CellSubsection subsection = updatable.getConcentrationContainer().getSubsection(reactant.getPreferredTopology());
            switch (reactant.getRole()) {
                case PRODUCT:
                    deltas.put(new ConcentrationDeltaIdentifier(updatable, subsection, reactant.getEntity()),
                            new ConcentrationDelta(this, subsection, reactant.getEntity(), UnitRegistry.concentration(velocity)));
                    break;
                case SUBSTRATE:
                    deltas.put(new ConcentrationDeltaIdentifier(updatable, subsection, reactant.getEntity()),
                            new ConcentrationDelta(this, subsection, reactant.getEntity(), UnitRegistry.concentration(-velocity)));
                    break;
                case CATALYTIC:
                    break;
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

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return new HashSet<>();
    }

    @Override
    public void scaleScalableFeatures() {
        kineticLaw.scaleScalableFeatures();
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new DynamicReactionBuilder(simulation);
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

        ParameterStep referenceParameter(String parameterIdentifier, double parameter, FeatureOrigin origin);

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
            module.catalysts = new ArrayList<>();
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
            module.getKineticLaw().referenceFeature(scalableFeature);
            return this;
        }

        @Override
        public ParameterStep referenceParameter(String parameterIdentifier, ScalableQuantityFeature<?> scalableFeature) {
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
        public ParameterStep referenceParameter(String parameterIdentifier, double parameter, FeatureOrigin origin) {
            module.getKineticLaw().referenceConstant(parameterIdentifier, parameter, origin);
            return this;
        }

        @Override
        public DynamicReaction build() {
            module.initialize();
            return module;
        }
    }

}
