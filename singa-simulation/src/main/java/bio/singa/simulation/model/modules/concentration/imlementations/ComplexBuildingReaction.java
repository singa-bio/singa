package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.features.reactions.BackwardsRateConstant;
import bio.singa.chemistry.features.reactions.ForwardsRateConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.exceptions.FeatureUnassignableException;
import bio.singa.features.model.Feature;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.modules.concentration.scope.IndependentUpdate;
import bio.singa.simulation.model.modules.concentration.specifity.UpdatableSpecific;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

import static bio.singa.features.parameters.Environment.getConcentrationUnit;

/**
 * Complex building or breaking reactions are a special kind of {@link ReversibleReaction}. In this kind of reaction
 * association or dissociation of chemical entities results in a change of section. For example when a ligand is bound
 * to a receptor the resulting complex is associated to the membrane section the receptor is located in.<br> Speaking in
 * terms of this module a <b>bindee</b> in the <b>bindee topology</b> is bound to a <b>binder</b> in the
 * <b>binder topology</b>, resulting in a <b>complex</b> in the <b>binder topology</b>.<br>
 * The speed of the reaction is guided by any {@link ForwardsRateConstant} that determines the speed of the association
 * reaction and a {@link BackwardsRateConstant} the determines the speed of the dissociation of the complex.
 *
 * Complex building reactions are {@link UpdatableSpecific} and supply {@link IndependentUpdate}s.
 *
 * <pre>
 *  // From: Lauffenburger, Douglas A., and Jennifer J. Linderman.
 *  //       Receptors: models for binding, trafficking, and signaling. Oxford University Press, 1996.
 *  //       Table on Page 30
 *
 *  // prazosin
 *  ChemicalEntity ligand = new SmallMolecule.Builder("ligand")
 *      .name("prazosin")
 *      .additionalIdentifier(new ChEBIIdentifier("CHEBI:8364"))
 *      .build();
 *
 *  // alpha-1 adrenergic receptor
 *  Receptor receptor = new Receptor.Builder("receptor")
 *      .name("alpha-1 adrenergic receptor")
 *      .additionalIdentifier(new UniProtIdentifier("P35348"))
 *      .build();
 *
 *  // the forwards rate constants
 *  RateConstant forwardsRate = RateConstant.create(2.4e8)
 *      .forward().secondOrder()
 *      .concentrationUnit(MOLE_PER_LITRE)
 *      .timeUnit(MINUTE)
 *      .build();
 *
 *  // the backwards rate constants
 *  RateConstant backwardsRate = RateConstant.create(0.018)
 *      .backward().firstOrder()
 *      .timeUnit(MINUTE)
 *      .build();
 *
 *  // create and add module
 *  ComplexBuildingReaction reaction = ComplexBuildingReaction.inSimulation(simulation)
 *      .identifier("binding reaction")
 *      .of(ligand, forwardsRate)
 *      .in(OUTER)
 *      .by(receptor, backwardsRate)
 *      .to(MEMBRANE)
 *      .build(); </pre>
 *
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
        // TODO apply condition
        setApplicationCondition(updatable -> true);
        // function
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, this::containsReactants);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(ForwardsRateConstant.class);
        getRequiredFeatures().add(BackwardsRateConstant.class);
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

    private boolean containsReactants(ConcentrationContainer concentrationContainer) {
        // binder and complex are zero
        if (concentrationContainer.get(binderTopology, binder).getValue().doubleValue() == 0.0
                && concentrationContainer.get(binderTopology, complex).getValue().doubleValue() == 0.0) {
            return false;
        }
        // bindee and complex are zero
        return !(concentrationContainer.get(bindeeTopology, bindee).getValue().doubleValue() == 0.0)
                || !(concentrationContainer.get(binderTopology, complex).getValue().doubleValue() == 0.0);
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
        final double complexConcentration = concentrationContainer.get(binderTopology, complex).getValue().doubleValue();
        // calculate velocity
        return forwardsRateConstant * binderConcentration * bindeeConcentration - backwardsRateConstant * complexConcentration;
    }

    public ComplexedChemicalEntity getComplex() {
        return complex;
    }

    public ChemicalEntity getBinder() {
        return binder;
    }

    public void setBinder(ChemicalEntity binder) {
        this.binder = binder;
    }

    public CellTopology getBinderTopology() {
        return binderTopology;
    }

    public void setBinderTopology(CellTopology binderTopology) {
        this.binderTopology = binderTopology;
    }

    public ChemicalEntity getBindee() {
        return bindee;
    }

    public void setBindee(ChemicalEntity bindee) {
        this.bindee = bindee;
    }

    public CellTopology getBindeeTopology() {
        return bindeeTopology;
    }

    public void setBindeeTopology(CellTopology bindeeTopology) {
        this.bindeeTopology = bindeeTopology;
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
