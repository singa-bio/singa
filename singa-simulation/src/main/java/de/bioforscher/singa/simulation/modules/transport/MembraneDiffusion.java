package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourDependentNodeSpecificModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.simulation.model.newsections.CellTopology.*;

/**
 * A permeant is crossing a membrane from side 1 to side 2. The flux JM is determined by terms of
 * JM = P * A * (c1 - c2)
 * where P is the {@link MembranePermeability}, A is the area of the membrane and c1 and c2 respectively are the
 * concentrations on the corresponding sides of the compartments.
 */
public class MembraneDiffusion extends AbstractNeighbourDependentNodeSpecificModule {

    public static CargoStep inSimulation(Simulation simulation) {
        return new MembraneDiffusionBuilder(simulation);
    }

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();

    static {
        requiredFeatures.add(MembranePermeability.class);
    }

    private ChemicalEntity cargo;

    public MembraneDiffusion(Simulation simulation) {
        super(simulation);
        // change of inner phase
        addDeltaFunction(this::calculateDeltas, this::hasMembrane);
    }

    private boolean hasMembrane(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getSubsection(MEMBRANE) != null;
    }

    public void initialize() {
        addReferencedEntity(cargo);
        // reference module in simulation
        addModuleToSimulation();
    }

    private Map<Updatable, Delta> calculateDeltas(ConcentrationContainer container) {
        Map<Updatable, Delta> deltas = new HashMap<>();
        if (currentUpdatable instanceof Vesicle) {
            handlePartialDistributionInVesicles(deltas, (Vesicle) currentUpdatable);
        } else {
            double value = calculateVelocity(container, container);
            deltas.put(currentUpdatable, new Delta(this, container.getInnerSubsection(), cargo, Quantities.getQuantity(value, Environment.getTransformedMolarConcentration())));
            deltas.put(currentUpdatable, new Delta(this, container.getOuterSubsection(), cargo, Quantities.getQuantity(-value, Environment.getTransformedMolarConcentration())));
        }
        return deltas;
    }

    private void handlePartialDistributionInVesicles(Map<Updatable, Delta> deltas, Vesicle vesicle) {
        Map<AutomatonNode, Quantity<Area>> associatedNodes = vesicle.getAssociatedNodes();
        double vesicleUpdate = 0.0;
        ConcentrationContainer vesicleContainer = vesicle.getConcentrationContainer();
        for (Map.Entry<AutomatonNode, Quantity<Area>> entry : associatedNodes.entrySet()) {
            AutomatonNode node = entry.getKey();
            ConcentrationContainer nodeContainer;
            if (halfTime) {
                nodeContainer = halfConcentrations.get(node);
            } else {
                nodeContainer = node.getConcentrationContainer();
            }
            double velocity = calculateVelocity(vesicleContainer, nodeContainer) * entry.getValue().getValue().doubleValue();
            vesicleUpdate += velocity;
            deltas.put(node, new Delta(this, nodeContainer.getInnerSubsection(), cargo, Quantities.getQuantity(-velocity, Environment.getTransformedMolarConcentration())));
        }
        deltas.put(vesicle, new Delta(this, vesicleContainer.getInnerSubsection(), cargo, Quantities.getQuantity(vesicleUpdate, Environment.getTransformedMolarConcentration())));
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
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
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
            module = new MembraneDiffusion(simulation);
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
