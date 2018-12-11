package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.features.Cargo;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.*;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.modules.concentration.scope.SemiDependentUpdate;
import bio.singa.simulation.model.modules.concentration.specifity.UpdatableSpecific;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import java.util.HashMap;
import java.util.Map;

import static bio.singa.features.model.Evidence.MANUALLY_ANNOTATED;

/**
 * The membrane diffusion module describes the movement of chemical entities across {@link Membrane}s driven by the
 * {@link MembranePermeability} of the cargo. The cargo molecules are crossing a membrane from one side to the other side.
 * The flux of concentration is determined by terms of
 * <pre>
 *  JM = Pd * A * (c1 - c2)</pre>
 * where Pd is the {@link MembranePermeability}, A is the area of the membrane, and c1 and c2 are the concentrations on
 * both sides of the membrane; as in:
 * <pre>
 *  Stein, Wilfred. Transport and diffusion across cell membranes. Elsevier, 2012.</pre>
 * This concentration based module applies {@link SemiDependentUpdate}s and is {@link UpdatableSpecific}.
 * <pre>
 *  // define the feature to parametrize the diffusion
 *  MembranePermeability membranePermeability = new MembranePermeability(Quantities.getQuantity(3.5E-03, CENTIMETRE_PER_SECOND),
 *         Evidence.MANUALLY_ANNOTATED);
 *  // assign it to the chemical entity
 *  SmallMolecule water = new SmallMolecule.Builder("water")
 *         .assignFeature(membranePermeability)
 *         .build();
 *  // create the module
 *  MembraneDiffusion.inSimulation(simulation)
 *         .cargo(water)
 *         .build();</pre>
 *
 * @author cl
 */
public class MembraneDiffusion extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    public static CargoStep inSimulation(Simulation simulation) {
        return new MembraneDiffusionBuilder(simulation);
    }

    private ChemicalEntity cargo;

    public void initialize() {
        // apply
        setApplicationCondition(this::hasMembrane);
        // function
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(MembranePermeability.class);
        // add cargo
        cargo = getFeature(Cargo.class).getFeatureContent();
        addReferencedEntity(cargo);
        // reference module in simulation
        addModuleToSimulation();
    }

    private boolean hasMembrane(Updatable updatable) {
        return updatable.getConcentrationContainer().getSubsection(CellTopology.MEMBRANE) != null;
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer container) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        Updatable currentUpdatable = supplier.getCurrentUpdatable();
        if (currentUpdatable instanceof Vesicle) {
            handlePartialDistributionInVesicles(deltas, (Vesicle) currentUpdatable);
        } else {
            Quantity<Area> membraneArea = ((AutomatonNode) currentUpdatable).getMembraneArea();
            double value = calculateVelocity(container, container) * membraneArea.getValue().doubleValue();
            deltas.put(new ConcentrationDeltaIdentifier(currentUpdatable, container.getInnerSubsection(), cargo),
                    new ConcentrationDelta(this, container.getInnerSubsection(), cargo, UnitRegistry.concentration(value)));
            deltas.put(new ConcentrationDeltaIdentifier(currentUpdatable, container.getOuterSubsection(), cargo),
                    new ConcentrationDelta(this, container.getOuterSubsection(), cargo, UnitRegistry.concentration(-value)));
        }
        return deltas;
    }

    private void handlePartialDistributionInVesicles(Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas, Vesicle vesicle) {
        Map<AutomatonNode, Double> associatedNodes = vesicle.getAssociatedNodes();
        double vesicleUpdate = 0.0;
        ConcentrationContainer vesicleContainer = vesicle.getConcentrationContainer();
        for (Map.Entry<AutomatonNode, Double> entry : associatedNodes.entrySet()) {
            AutomatonNode node = entry.getKey();
            ConcentrationContainer nodeContainer;
            if (supplier.isStrutCalculation()) {
                nodeContainer = getScope().getHalfStepConcentration(node);
            } else {
                nodeContainer = node.getConcentrationContainer();
            }
            // TODO scale with area
            double velocity = calculateVelocity(nodeContainer, vesicleContainer) * entry.getValue();
            vesicleUpdate += velocity;
            deltas.put(new ConcentrationDeltaIdentifier(node, nodeContainer.getInnerSubsection(), cargo),
                    new ConcentrationDelta(this, nodeContainer.getInnerSubsection(), cargo, UnitRegistry.concentration(velocity)));
        }
        deltas.put(new ConcentrationDeltaIdentifier(vesicle, vesicleContainer.getOuterSubsection(), cargo),
                new ConcentrationDelta(this, vesicleContainer.getOuterSubsection(), cargo, UnitRegistry.concentration(-vesicleUpdate)));
    }

    private double calculateVelocity(ConcentrationContainer innerContainer, ConcentrationContainer outerContainer) {
        final double permeability = getScaledFeature(cargo, MembranePermeability.class).getValue().doubleValue();
        return getCargoDifference(innerContainer, outerContainer) * permeability;
    }

    private double getCargoDifference(ConcentrationContainer innerContainer, ConcentrationContainer outerContainer) {
        double outerConcentration;
        double innerConcentration;
        outerConcentration = outerContainer.get(CellTopology.OUTER, cargo).getValue().doubleValue();
        innerConcentration = innerContainer.get(CellTopology.INNER, cargo).getValue().doubleValue();
        // return delta
        return outerConcentration - innerConcentration;
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new MembraneDiffusionBuilder(simulation);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + cargo.getName() + ")";
    }

    public interface CargoStep {
        BuildStep cargo(ChemicalEntity cargo);
    }

    public interface BuildStep {
        MembraneDiffusion build();
    }

    public static class MembraneDiffusionBuilder implements CargoStep, BuildStep, ModuleBuilder {

        private MembraneDiffusion module;

        public MembraneDiffusionBuilder(Simulation simulation) {
            createModule(simulation);
        }

        @Override
        public MembraneDiffusion getModule() {
            return module;
        }

        @Override
        public MembraneDiffusion createModule(Simulation simulation) {
            module = ModuleFactory.setupModule(MembraneDiffusion.class,
                    ModuleFactory.Scope.SEMI_NEIGHBOURHOOD_DEPENDENT,
                    ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
            module.setSimulation(simulation);
            return module;
        }

        @Override
        public BuildStep cargo(ChemicalEntity cargo) {
            module.setFeature(new Cargo(cargo, MANUALLY_ANNOTATED));
            return this;
        }

        @Override
        public MembraneDiffusion build() {
            module.initialize();
            return module;
        }

    }

}
