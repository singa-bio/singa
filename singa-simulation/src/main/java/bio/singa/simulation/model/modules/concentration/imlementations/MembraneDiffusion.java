package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.features.parameters.Environment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.modules.concentration.scope.SemiDependentUpdate;
import bio.singa.simulation.model.modules.concentration.specifity.UpdatableSpecific;
import bio.singa.simulation.model.modules.displacement.Vesicle;
import bio.singa.simulation.model.modules.macroscopic.membranes.Membrane;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import java.util.HashMap;
import java.util.Map;

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
 *         FeatureOrigin.MANUALLY_ANNOTATED);
 *
 *  // assign it to the chemical entity
 *  SmallMolecule water = new SmallMolecule.Builder("water")
 *         .assignFeature(membranePermeability)
 *         .build();
 *
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
            deltas.put(new ConcentrationDeltaIdentifier(currentUpdatable, container.getInnerSubsection(), cargo), new ConcentrationDelta(this, container.getInnerSubsection(), cargo, Quantities.getQuantity(value, Environment.getConcentrationUnit())));
            deltas.put(new ConcentrationDeltaIdentifier(currentUpdatable, container.getOuterSubsection(), cargo), new ConcentrationDelta(this, container.getOuterSubsection(), cargo, Quantities.getQuantity(-value, Environment.getConcentrationUnit())));
        }
        return deltas;
    }

    private void handlePartialDistributionInVesicles(Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas, Vesicle vesicle) {
        Map<AutomatonNode, Quantity<Area>> associatedNodes = vesicle.getAssociatedNodes();
        double vesicleUpdate = 0.0;
        ConcentrationContainer vesicleContainer = vesicle.getConcentrationContainer();
        for (Map.Entry<AutomatonNode, Quantity<Area>> entry : associatedNodes.entrySet()) {
            AutomatonNode node = entry.getKey();
            ConcentrationContainer nodeContainer;
            if (supplier.isStrutCalculation()) {
                nodeContainer = getScope().getHalfStepConcentration(node);
            } else {
                nodeContainer = node.getConcentrationContainer();
            }
            Quantity<Area> area = entry.getValue();
            double velocity = calculateVelocity(vesicleContainer, nodeContainer) * area.getValue().doubleValue();
            vesicleUpdate += velocity;
            deltas.put(new ConcentrationDeltaIdentifier(node, nodeContainer.getInnerSubsection(), cargo),
                    new ConcentrationDelta(this, nodeContainer.getInnerSubsection(), cargo, Quantities.getQuantity(-velocity, Environment.getConcentrationUnit())));
        }
        deltas.put(new ConcentrationDeltaIdentifier(vesicle, vesicleContainer.getInnerSubsection(), cargo),
                new ConcentrationDelta(this, vesicleContainer.getInnerSubsection(), cargo, Quantities.getQuantity(vesicleUpdate, Environment.getConcentrationUnit())));
    }

    private double calculateVelocity(ConcentrationContainer innerContainer, ConcentrationContainer outerContainer) {
        final double permeability = getScaledFeature(cargo, MembranePermeability.class).getValue().doubleValue();
        return getCargoDifference(innerContainer, outerContainer) * permeability;
    }

    private double getCargoDifference(ConcentrationContainer innerContainer, ConcentrationContainer outerContainer) {
        double outerConcentration;
        double innerConcentration;
        if (innerContainer == outerContainer) {
            outerConcentration = outerContainer.get(CellTopology.OUTER, cargo).getValue().doubleValue();
            innerConcentration = innerContainer.get(CellTopology.INNER, cargo).getValue().doubleValue();
        } else {
            outerConcentration = outerContainer.get(CellTopology.INNER, cargo).getValue().doubleValue();
            innerConcentration = innerContainer.get(CellTopology.INNER, cargo).getValue().doubleValue();
        }
        // return delta
        return outerConcentration - innerConcentration;
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

    public static class MembraneDiffusionBuilder implements CargoStep, BuildStep {

        private MembraneDiffusion module;

        public MembraneDiffusionBuilder(Simulation simulation) {
            module = ModuleFactory.setupModule(MembraneDiffusion.class,
                    ModuleFactory.Scope.SEMI_NEIGHBOURHOOD_DEPENDENT,
                    ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
            module.setSimulation(simulation);
        }

        @Override
        public BuildStep cargo(ChemicalEntity cargo) {
            module.cargo = cargo;
            return this;
        }

        @Override
        public MembraneDiffusion build() {
            module.initialize();
            return module;
        }

    }

}
