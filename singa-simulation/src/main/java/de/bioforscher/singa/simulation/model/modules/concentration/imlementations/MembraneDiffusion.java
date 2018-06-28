package de.bioforscher.singa.simulation.model.modules.concentration.imlementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import de.bioforscher.singa.simulation.model.modules.concentration.ModuleFactory;
import de.bioforscher.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import de.bioforscher.singa.simulation.model.simulation.Updatable;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import java.util.HashMap;
import java.util.Map;

import static de.bioforscher.singa.simulation.model.sections.CellTopology.*;

/**
 * A permeant is crossing a membrane from side 1 to side 2. The flux JM is determined by terms of
 * JM = P * A * (c1 - c2)
 * where P is the {@link MembranePermeability}, A is the area of the membrane, and c1 and c2 are the
 * concentrations on the corresponding sides of the compartments.
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
        return updatable.getConcentrationContainer().getSubsection(MEMBRANE) != null;
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer container) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        Updatable currentUpdatable = supplier.getCurrentUpdatable();
        if (currentUpdatable instanceof Vesicle) {
            handlePartialDistributionInVesicles(deltas, (Vesicle) currentUpdatable);
        } else {
            double value = calculateVelocity(container, container) * Environment.getSubsectionArea().getValue().doubleValue();
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
            outerConcentration = outerContainer.get(OUTER, cargo).getValue().doubleValue();
            innerConcentration = innerContainer.get(INNER, cargo).getValue().doubleValue();
        } else {
            outerConcentration = outerContainer.get(INNER, cargo).getValue().doubleValue();
            innerConcentration = innerContainer.get(INNER, cargo).getValue().doubleValue();
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
