package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.exceptions.FeatureUnassignableException;
import bio.singa.features.model.Feature;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import bio.singa.simulation.model.modules.concentration.reactants.Reactant;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MichaelisMentenReaction extends Reaction {

    public static MichaelisMentenReactionBuilder inSimulation(Simulation simulation) {
        return new MichaelisMentenReactionBuilder(simulation);
    }

    private ChemicalEntity enzyme;

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

    protected List<ConcentrationDelta> calculateDeltas(ConcentrationContainer concentrationContainer) {
        if (enzyme.isMembraneAnchored()) {
            List<ConcentrationDelta> deltas = new ArrayList<>();
            if (supplier.getCurrentSubsection().equals(concentrationContainer.getMembraneSubsection())){
                double velocity = calculateMembraneBasedVelocity(concentrationContainer);
                for (Reactant substrate : substrates) {
                    double deltaValue = -velocity * substrate.getStoichiometricNumber();
                    deltas.add(new ConcentrationDelta(this, concentrationContainer.getSubsection(substrate.getPreferredTopology()), substrate.getEntity(), UnitRegistry.concentration(deltaValue)));
                }
                for (Reactant product : products) {
                    double deltaValue = velocity * product.getStoichiometricNumber();
                    deltas.add(new ConcentrationDelta(this, concentrationContainer.getSubsection(product.getPreferredTopology()), product.getEntity(), UnitRegistry.concentration(deltaValue)));
                }
            }
            return deltas;
        }
        return super.calculateDeltas(concentrationContainer);
    }

    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity<FirstOrderRate> kCat = getScaledFeature(TurnoverNumber.class);
        final Quantity<MolarConcentration> km = getFeature(MichaelisConstant.class).getFeatureContent().to(UnitRegistry.getConcentrationUnit());
        // (KCAT * enzyme * substrate) / KM + substrate
        // FIXME currently "only" the first substrate is considered
        double substrateConcentration = concentrationContainer.get(supplier.getCurrentSubsection(), getSubstrateEntities().iterator().next()).getValue().doubleValue();
        double enzymeConcentration = concentrationContainer.get(supplier.getCurrentSubsection(), enzyme).getValue().doubleValue();
        return (kCat.getValue().doubleValue() * enzymeConcentration * substrateConcentration) / (km.getValue().doubleValue() + substrateConcentration);
    }

    public double calculateMembraneBasedVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity<FirstOrderRate> kCat = getScaledFeature(TurnoverNumber.class);
        final Quantity<MolarConcentration> km = getFeature(MichaelisConstant.class).getFeatureContent().to(UnitRegistry.getConcentrationUnit());
        // (KCAT * enzyme * substrate) / KM + substrate
        // FIXME currently "only" the first substrate is considered
        Reactant reactant = getSubstrates().iterator().next();
        double substrateConcentration = concentrationContainer.get(reactant.getPreferredTopology(), reactant.getEntity()).getValue().doubleValue();
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

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new MichaelisMentenReactionBuilder(simulation);
    }

    public static class MichaelisMentenReactionBuilder extends Reaction.Builder<MichaelisMentenReaction, MichaelisMentenReactionBuilder> {

        public MichaelisMentenReactionBuilder(Simulation simulation) {
            super(simulation);
        }

        @Override
        protected MichaelisMentenReaction createObject(Simulation simulation) {
            MichaelisMentenReaction module = ModuleFactory.setupModule(MichaelisMentenReaction.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.SECTION_SPECIFIC);
            module.setSimulation(simulation);
            return module;
        }



        public MichaelisMentenReactionBuilder enzyme(ChemicalEntity enzyme) {
            topLevelObject.enzyme = enzyme;
            topLevelObject.addReferencedEntity(enzyme);
            topLevelObject.setFeature(enzyme.getFeature(TurnoverNumber.class));
            topLevelObject.setFeature(enzyme.getFeature(MichaelisConstant.class));
            return this;
        }

        @Override
        protected MichaelisMentenReactionBuilder getBuilder() {
            return this;
        }

    }

}
