package de.bioforscher.singa.simulation.model.modules.concentration.imlementations;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.features.diffusivity.Diffusivity;
import de.bioforscher.singa.chemistry.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.chemistry.features.reactions.RateConstant;
import de.bioforscher.singa.features.exceptions.FeatureUnassignableException;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import de.bioforscher.singa.simulation.model.modules.concentration.ModuleFactory;
import de.bioforscher.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.sections.CellTopology;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

import static de.bioforscher.singa.features.parameters.Environment.getConcentrationUnit;

/**
 * @author cl
 */
public class ComplexBuildingReaction extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    public static BindeeSelection inSimulation(Simulation simulation) {
        return new BindingBuilder(simulation);
    }

    private RateConstant forwardsReactionRate;
    private RateConstant backwardsReactionRate;

    private ChemicalEntity binder;
    private CellTopology binderTopology;

    private ChemicalEntity bindee;
    private CellTopology bindeeTopology;

    private ComplexedChemicalEntity complex;

    public void initialize() {
        // apply
        setApplicationCondition(updatable -> true);
        // function
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(Diffusivity.class);
        // in no complex has been set create it
        if (complex == null) {
            complex = new ComplexedChemicalEntity.Builder(binder.getIdentifier().getIdentifier() + ":" + bindee.getIdentifier().getIdentifier())
                    .addAssociatedPart(binder)
                    .addAssociatedPart(bindee)
                    .build();
        }
        // reference entities for this module
        addReferencedEntity(bindee);
        addReferencedEntity(binder);
        addReferencedEntity(complex);
        // reference module in simulation
        addModuleToSimulation();
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        double velocity = calculateVelocity(concentrationContainer);
        // ligand concentration
        CellSubsection bindeeSubsection = concentrationContainer.getSubsection(bindeeTopology);
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), bindeeSubsection, bindee),
                new ConcentrationDelta(this, bindeeSubsection, bindee, Quantities.getQuantity(-velocity, getConcentrationUnit())));
        // unbound receptor concentration
        CellSubsection binderSubsection = concentrationContainer.getSubsection(binderTopology);
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), binderSubsection, binder),
                new ConcentrationDelta(this, binderSubsection, binder, Quantities.getQuantity(-velocity, getConcentrationUnit())));
        // bound receptor concentration
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), binderSubsection, complex),
                new ConcentrationDelta(this, binderSubsection, complex, Quantities.getQuantity(velocity, getConcentrationUnit())));
        return deltas;
    }

    private double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // get rates
        final double forwardsRateConstant = getScaledForwardsReactionRate().getValue().doubleValue();
        final double backwardsRateConstant = getScaledBackwardsReactionRate().getValue().doubleValue();
        // get concentrations
        final double bindeeConcentration = concentrationContainer.get(bindeeTopology, bindee).getValue().doubleValue();
        final double binderConcentration = concentrationContainer.get(binderTopology, binder).getValue().doubleValue();
        final double complexConcentration = concentrationContainer.get(bindeeTopology, complex).getValue().doubleValue();
        // calculate velocity
        return forwardsRateConstant * binderConcentration * bindeeConcentration - backwardsRateConstant * complexConcentration;
    }


    public ComplexedChemicalEntity getComplex() {
        return complex;
    }

    public String getReactionString() {
        String substrates = binder.getIdentifier() + " + " + bindee.getIdentifier();
        String products = complex.getIdentifier().toString();
        return substrates + " \u21CB " + products;
    }

    public String getStringForProtocol() {
        return getClass().getSimpleName() + " summary:" + System.lineSeparator() +
                "  " + "primary identifier: " + getIdentifier() + System.lineSeparator() +
                "  " + "reaction: " + getReactionString() + System.lineSeparator() +
                "  " + "binding: " + bindee.getIdentifier() + " is bound to " + binder.getIdentifier() + System.lineSeparator() +
                "  " + "features: " + System.lineSeparator() +
                listFeatures("    ");
    }

    @Override
    public void checkFeatures() {
        boolean forwardsRateFound = false;
        boolean backwardsRateFound = false;
        for (Feature<?> feature : getFeatures()) {
            // any forwards rate constant
            if (feature instanceof ForwardsRateConstant) {
                forwardsRateFound = true;
            }
            // any backwards rate constant
            if (feature instanceof BackwardsRateConstant) {
                backwardsRateFound = true;
            }
        }
        if (!forwardsRateFound || !backwardsRateFound) {
            throw new FeatureUnassignableException("Required reaction rates unavailable.");
        }
    }

    private Quantity getScaledForwardsReactionRate() {
        if (forwardsReactionRate == null) {
            for (Feature<?> feature : getFeatures()) {
                // any forwards rate constant
                if (feature instanceof ForwardsRateConstant) {
                    forwardsReactionRate = (RateConstant) feature;
                    break;
                }
            }
        }
        if (supplier.isStrutCalculation()) {
            return forwardsReactionRate.getHalfScaledQuantity();
        }
        return forwardsReactionRate.getScaledQuantity();
    }

    private Quantity getScaledBackwardsReactionRate() {
        if (backwardsReactionRate == null) {
            for (Feature<?> feature : getFeatures()) {
                // any forwards rate constant
                if (feature instanceof BackwardsRateConstant) {
                    backwardsReactionRate = (RateConstant) feature;
                    break;
                }
            }
        }
        if (supplier.isStrutCalculation()) {
            return backwardsReactionRate.getHalfScaledQuantity();
        }
        return backwardsReactionRate.getScaledQuantity();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getIdentifier() + " (" + getReactionString() + ")";
    }

    public interface BindeeSelection {
        BindeeSelection identifier(String identifier);

        BindeeSectionSelection of(ChemicalEntity bindee);

        BindeeSectionSelection of(ChemicalEntity bindee, RateConstant forwardsRateConstant);
    }

    public interface BindeeSectionSelection {
        BinderSelection in(CellTopology bindeeTopology);
    }

    public interface BinderSelection {
        BinderSectionSelection by(ChemicalEntity binder);

        BinderSectionSelection by(ChemicalEntity binder, RateConstant forwardsRateConstant);
    }

    public interface BinderSectionSelection {
        BuilderStep to(CellTopology binderTopology);
    }

    public interface BuilderStep {
        BuilderStep formingComplex(ComplexedChemicalEntity complex);

        ComplexBuildingReaction build();
    }

    public static class BindingBuilder implements BinderSelection, BinderSectionSelection, BindeeSelection, BindeeSectionSelection, BuilderStep {

        private ComplexBuildingReaction module;

        public BindingBuilder(Simulation simulation) {
            module = ModuleFactory.setupModule(ComplexBuildingReaction.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
            module.setSimulation(simulation);
        }

        @Override
        public BindeeSelection identifier(String identifier) {
            module.setIdentifier(identifier);
            return this;
        }

        @Override
        public BindeeSectionSelection of(ChemicalEntity bindee) {
            module.bindee = bindee;
            return this;
        }

        @Override
        public BindeeSectionSelection of(ChemicalEntity binder, RateConstant forwardsRateConstant) {
            module.setFeature(forwardsRateConstant);
            return of(binder);
        }

        @Override
        public BinderSelection in(CellTopology binderTopology) {
            module.bindeeTopology = binderTopology;
            return this;
        }

        @Override
        public BinderSectionSelection by(ChemicalEntity binder) {
            module.binder = binder;
            return this;
        }

        @Override
        public BinderSectionSelection by(ChemicalEntity binder, RateConstant backwardsRateConstant) {
            module.setFeature(backwardsRateConstant);
            return by(binder);
        }

        @Override
        public BuilderStep to(CellTopology binderTopology) {
            module.binderTopology = binderTopology;
            return this;
        }

        @Override
        public BuilderStep formingComplex(ComplexedChemicalEntity complex) {
            module.complex = complex;
            return this;
        }

        @Override
        public ComplexBuildingReaction build() {
            module.initialize();
            return module;
        }
    }

}
