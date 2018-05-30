package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.OUTER;

/**
 * A permeant is crossing a membrane from side 1 to side 2. The flux JM is determined by terms of
 *
 * JM = P * A * (c1 - c2)
 *
 * where P is the {@link MembranePermeability}, A is the area of the membrane and c1 and c2 respectively are the
 * concentrations on the corresponding sides of the compartments.
 *
 */
public class MembraneDiffusion extends AbstractNeighbourIndependentModule {

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
        // apply this module only to membranes

        // change of inner phase
        addDeltaFunction(this::calculateInnerPhaseDelta, this::onlyInnerPhase);
        // change of outer phase
        addDeltaFunction(this::calculateOuterPhaseDelta, this::onlyOuterPhase);
    }

    public void initialize() {
        // reference module in simulation
        addModuleToSimulation();
    }

    private Delta calculateOuterPhaseDelta(ConcentrationContainer container) {
        final ChemicalEntity entity = getCurrentChemicalEntity();
        double value;
        if (entity.equals(cargo)) {
            final double permeability = getScaledFeature(cargo, MembranePermeability.class).getValue().doubleValue();
            value = getCargoDelta(container) * permeability;
        } else {
            value = 0.0;
        }
        return new Delta(this, container.getOuterSubsection(), entity, Quantities.getQuantity(value, Environment.getTransformedMolarConcentration()));
    }

    private Delta calculateInnerPhaseDelta(ConcentrationContainer container) {
        final ChemicalEntity entity = getCurrentChemicalEntity();
        double value;
        if (entity.equals(cargo)) {
            final double permeability = getScaledFeature(cargo, MembranePermeability.class).getValue().doubleValue();
            value = getCargoDelta(container) * permeability;
        } else {
            value = 0.0;
        }
        return new Delta(this, container.getInnerSubsection(), entity, Quantities.getQuantity(value, Environment.getTransformedMolarConcentration()));
    }

    private double getCargoDelta(ConcentrationContainer container) {
        // sum outer solutes
        double outerConcentration = container.get(OUTER, cargo).getValue().doubleValue();
        // sum inner solutes
        double innerConcentration = container.get(INNER, cargo).getValue().doubleValue();
        // return delta
        return isInnerPhase(container) ? outerConcentration - innerConcentration : innerConcentration - outerConcentration;
    }

    /**
     * Only apply, if this is the outer phase, the outer phase contains the cargo and the inner layer contains the
     * transporter.
     *
     * @param container
     * @return
     */
    private boolean onlyOuterPhase(ConcentrationContainer container) {
        return isOuterPhase(container) && isCargo();
    }

    /**
     * Only apply, if this is the inner phase, the outer phase contains the cargo and the inner layer contains the
     * transporter.
     *
     * @param container
     * @return
     */
    private boolean onlyInnerPhase(ConcentrationContainer container) {
        return isInnerPhase(container) && isCargo();
    }

    private boolean isOuterPhase(ConcentrationContainer container) {
        return getCurrentCellSection().equals(container.getOuterSubsection());
    }

    private boolean isInnerPhase(ConcentrationContainer container) {
        return getCurrentCellSection().equals(container.getInnerSubsection());
    }

    private boolean isCargo() {
        return getCurrentChemicalEntity().equals(cargo);
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+" ("+cargo.getName()+")";
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
