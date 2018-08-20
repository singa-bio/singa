package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.reactions.FirstOrderRate;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.exceptions.FeatureUnassignableException;
import bio.singa.features.model.Feature;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.SectionDeltaFunction;
import bio.singa.simulation.model.modules.concentration.reactants.StoichiometricReactant;
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

    public static Builder inSimulation(Simulation simulation) {
        return new Builder(simulation);
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
                for (StoichiometricReactant substrate : substrates) {
                    double deltaValue = -velocity * substrate.getStoichiometricNumber();
                    deltas.add(new ConcentrationDelta(this, concentrationContainer.getSubsection(substrate.getPrefferedTopology()), substrate.getEntity(), Quantities.getQuantity(deltaValue, Environment.getConcentrationUnit())));
                }
                for (StoichiometricReactant product : products) {
                    double deltaValue = velocity * product.getStoichiometricNumber();
                    deltas.add(new ConcentrationDelta(this, concentrationContainer.getSubsection(product.getPrefferedTopology()), product.getEntity(), Quantities.getQuantity(deltaValue, Environment.getConcentrationUnit())));
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
        final Quantity<MolarConcentration> km = getFeature(MichaelisConstant.class).getFeatureContent().to(Environment.getConcentrationUnit());
        // (KCAT * enzyme * substrate) / KM + substrate
        // FIXME currently "only" the first substrate is considered
        double substrateConcentration = concentrationContainer.get(supplier.getCurrentSubsection(), getSubstrateEntities().iterator().next()).getValue().doubleValue();
        double enzymeConcentration = concentrationContainer.get(supplier.getCurrentSubsection(), enzyme).getValue().doubleValue();
        return (kCat.getValue().doubleValue() * enzymeConcentration * substrateConcentration) / (km.getValue().doubleValue() + substrateConcentration);
    }

    public double calculateMembraneBasedVelocity(ConcentrationContainer concentrationContainer) {
        // reaction rates for this reaction
        final Quantity<FirstOrderRate> kCat = getScaledFeature(TurnoverNumber.class);
        final Quantity<MolarConcentration> km = getFeature(MichaelisConstant.class).getFeatureContent().to(Environment.getConcentrationUnit());
        // (KCAT * enzyme * substrate) / KM + substrate
        // FIXME currently "only" the first substrate is considered
        StoichiometricReactant reactant = getSubstrates().iterator().next();
        double substrateConcentration = concentrationContainer.get(reactant.getPrefferedTopology(), reactant.getEntity()).getValue().doubleValue();
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

        public Builder enzyme(ChemicalEntity enzyme) {
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
