package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.HashSet;
import java.util.Set;

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
        onlyApplyIf(node -> node.getState().equals(CellSectionState.MEMBRANE));
        // change of inner phase
        addDeltaFunction(this::calculateInnerPhaseDelta, this::onlyInnerPhase);
        // change of outer phase
        addDeltaFunction(this::calculateOuterPhaseDelta, this::onlyOuterPhase);
    }

    public void initialize() {
        // reference module in simulation
        addModuleToSimulation();
    }

    private Delta calculateOuterPhaseDelta(ConcentrationContainer concentrationContainer) {
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        double value;
        if (entity.equals(cargo)) {
            final double permeability = getScaledFeature(cargo, MembranePermeability.class).getValue().doubleValue();
            value = getCargoDelta(membraneContainer) * permeability;
        } else {
            value = 0.0;
        }
        return new Delta(this, membraneContainer.getOuterPhaseSection(), entity, Quantities.getQuantity(value, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

    private Delta calculateInnerPhaseDelta(ConcentrationContainer concentrationContainer) {
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        double value;
        if (entity.equals(cargo)) {
            final double permeability = getScaledFeature(cargo, MembranePermeability.class).getValue().doubleValue();
            value = getCargoDelta(membraneContainer) * permeability;
        } else {
            value = 0.0;
        }
        return new Delta(this, membraneContainer.getInnerPhaseSection(), entity, Quantities.getQuantity(value, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

    private double getCargoDelta(MembraneContainer container) {
        // sum outer solutes
        double outerConcentration = container.getOuterPhaseConcentration(cargo).getValue().doubleValue();
        // sum inner solutes
        double innerConcentration = container.getInnerPhaseConcentration(cargo).getValue().doubleValue();
        // return delta
        return isInnerPhase(container) ? outerConcentration - innerConcentration : innerConcentration - outerConcentration;
    }

    /**
     * Only apply, if this is the outer phase, the outer phase contains the cargo and the inner layer contains the
     * transporter.
     *
     * @param concentrationContainer
     * @return
     */
    private boolean onlyOuterPhase(ConcentrationContainer concentrationContainer) {
        MembraneContainer container = (MembraneContainer) concentrationContainer;
        return isOuterPhase(container) && isCargo();
    }

    /**
     * Only apply, if this is the inner phase, the outer phase contains the cargo and the inner layer contains the
     * transporter.
     *
     * @param concentrationContainer
     * @return
     */
    private boolean onlyInnerPhase(ConcentrationContainer concentrationContainer) {
        MembraneContainer container = (MembraneContainer) concentrationContainer;
        return isInnerPhase(container) && isCargo();
    }

    private boolean isOuterPhase(MembraneContainer container) {
        return getCurrentCellSection().equals(container.getOuterPhaseSection());
    }

    private boolean isInnerPhase(MembraneContainer container) {
        return getCurrentCellSection().equals(container.getInnerPhaseSection());
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
