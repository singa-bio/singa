package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.OsmoticPermeability;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class SingleFileChannelMembraneTransport extends AbstractNeighbourIndependentModule {

    public static TransporterStep inSimulation(Simulation simulation) {
        return new SingleFileChannelMembraneTransportBuilder(simulation);
    }

    private Transporter transporter;
    private ChemicalEntity cargo;
    private Set<ChemicalEntity> solutes;

    private SingleFileChannelMembraneTransport(Simulation simulation) {
        super(simulation);
        solutes = new HashSet<>();
        // apply this module only to membranes
        onlyApplyIf(node -> node.getState().equals(CellSectionState.MEMBRANE));
        // change of inner phase
        addDeltaFunction(this::calculateInnerPhaseDelta, this::onlyInnerPhase);
        // change of outer phase
        addDeltaFunction(this::calculateOuterPhaseDelta, this::onlyOuterPhase);
    }

    private void initialize() {
        // reference entities for this module
        addReferencedEntity(transporter);
        addReferencedEntity(cargo);
        addReferencedEntities(solutes);
        // reference module in simulation
        addModuleToSimulation();
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
        return isOuterPhase(container) && isCargo() && transporterInMembrane(container);
    }

    private Delta calculateOuterPhaseDelta(ConcentrationContainer concentrationContainer) {
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        double value;
        if (entity.equals(cargo)) {
            final double permeability = getScaledFeature(transporter, OsmoticPermeability.class).getValue().doubleValue();
            value = getSoluteDelta(membraneContainer) * permeability * membraneContainer.getInnerMembraneLayerConcentration(transporter).getValue().doubleValue() * 10000;
        } else {
            value = 0.0;
        }
        return new Delta(this, membraneContainer.getOuterPhaseSection(), entity, Quantities.getQuantity(value, EnvironmentalParameters.getTransformedMolarConcentration()));
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
        return isInnerPhase(container) && isCargo() && transporterInMembrane(container);
    }

    private Delta calculateInnerPhaseDelta(ConcentrationContainer concentrationContainer) {
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        double value;
        if (entity.equals(cargo)) {
            final double permeability = getScaledFeature(transporter, OsmoticPermeability.class).getValue().doubleValue();
            value = getSoluteDelta(membraneContainer) * permeability * membraneContainer.getInnerMembraneLayerConcentration(transporter).getValue().doubleValue() * 10000;
        } else {
            value = 0.0;
        }
        return new Delta(this, membraneContainer.getInnerPhaseSection(), entity, Quantities.getQuantity(value, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

    private double getSoluteDelta(MembraneContainer container) {
        // sum outer solutes
        double outerConcentration = 0.0;
        for (ChemicalEntity solute : solutes) {
            outerConcentration += container.getOuterPhaseConcentration(solute).getValue().doubleValue();
        }
        // sum inner solutes
        double innerConcentration = 0.0;
        for (ChemicalEntity solute : solutes) {
            innerConcentration += container.getInnerPhaseConcentration(solute).getValue().doubleValue();
        }
        // return delta
        return isInnerPhase(container) ?  innerConcentration - outerConcentration : outerConcentration - innerConcentration;
    }

    private boolean isCargo() {
        return getCurrentChemicalEntity().equals(cargo);
    }

    private boolean transporterInMembrane(MembraneContainer container) {
        return container.getAvailableConcentration(container.getInnerLayerSection(), transporter).getValue().doubleValue() != 0.0;
    }

    private boolean isOuterPhase(MembraneContainer container) {
        return getCurrentCellSection().equals(container.getOuterPhaseSection());
    }

    private boolean isInnerPhase(MembraneContainer container) {
        return getCurrentCellSection().equals(container.getInnerPhaseSection());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + transporter.getName() + ")";
    }


    public interface TransporterStep {
        CargoStep transporter(Transporter transporter);
    }

    public interface CargoStep {
        SolutesStep cargo(ChemicalEntity cargo);
    }

    public interface SolutesStep {
        SolutesStep forSolutes(ChemicalEntity chemicalEntity);

        SolutesStep forSolutes(ChemicalEntity ... chemicalEntities );

        SolutesStep forSolutes(Collection<ChemicalEntity> chemicalEntities);
    }

    public interface BuildStep {
        SingleFileChannelMembraneTransport build();
    }

    public static class SingleFileChannelMembraneTransportBuilder implements TransporterStep, CargoStep, SolutesStep, BuildStep {

        SingleFileChannelMembraneTransport module;

        public SingleFileChannelMembraneTransportBuilder(Simulation simulation) {
            module = new SingleFileChannelMembraneTransport(simulation);
        }

        @Override
        public CargoStep transporter(Transporter transporter) {
            module.transporter = transporter;
            return this;
        }

        @Override
        public SolutesStep cargo(ChemicalEntity cargo) {
            module.cargo = cargo;
            return this;
        }

        @Override
        public SolutesStep forSolutes(ChemicalEntity solute) {
            module.solutes.add(solute);
            return this;
        }

        @Override
        public SolutesStep forSolutes(ChemicalEntity... solutes) {
            module.solutes.addAll(Arrays.asList(solutes));
            return this;
        }

        @Override
        public SolutesStep forSolutes(Collection<ChemicalEntity> solutes) {
            module.solutes.addAll(solutes);
            return this;
        }

        @Override
        public SingleFileChannelMembraneTransport build() {
            module.initialize();
            return module;
        }
    }

}
