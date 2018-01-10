package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Transporter;
import de.bioforscher.singa.chemistry.descriptive.features.transporterflux.TransporterFlux;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.structure.features.molarvolume.MolarVolume;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.singa.features.quantities.NaturalConstants.BOLTZMANN_CONSTANT;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class MediatedMembraneTransport extends AbstractNeighbourIndependentModule {

    private Transporter transporter;
    private ChemicalEntity<?> cargo;

    private double cargoConstant;

    public MediatedMembraneTransport(Simulation simulation, Transporter transporter, ChemicalEntity cargo) {
        super(simulation);
        this.transporter = transporter;
        this.cargo = cargo;
        // calculate cargo constant
        cargoConstant = calculateCargoConstant(cargo);
        // apply this module only to membranes
        onlyApplyIf(node -> node.getState().equals(NodeState.MEMBRANE));
        // change of inner phase
        addDeltaFunction(this::calculateInnerPhaseDelta, this::onlyInnerPhase);
        // change of outer phase
        addDeltaFunction(this::calculateOuterPhaseDelta, this::onlyOuterPhase);
    }

    private static double calculateCargoConstant(ChemicalEntity<?> cargo) {
        double cargoVolume = cargo.getFeature(MolarVolume.class).getValue().doubleValue();
        return -BOLTZMANN_CONSTANT.getValue().doubleValue() * EnvironmentalParameters.getInstance().getSystemTemperature().getValue().doubleValue() * cargoVolume;
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
        final ChemicalEntity<?> entity = getCurrentChemicalEntity();
        final MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        double value;
        if (entity.equals(cargo)) {
            final double flux = getFeature(transporter, TransporterFlux.class).getValue().doubleValue();
            final double deltaCargo = membraneContainer.getOuterPhaseConcentration(cargo).getValue().doubleValue() - membraneContainer.getInnerPhaseConcentration(cargo).getValue().doubleValue();
            value = cargoConstant * deltaCargo * flux * membraneContainer.getInnerMembraneLayerConcentration(transporter).getValue().doubleValue();
        } else {
            value = 0.0;
        }
        final Delta delta = new Delta(membraneContainer.getOuterPhaseSection(), entity, Quantities.getQuantity(value, MOLE_PER_LITRE));
        return delta;
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
        final ChemicalEntity<?> entity = getCurrentChemicalEntity();
        final MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        double value;
        if (entity.equals(cargo)) {
            final double flux = getFeature(transporter, TransporterFlux.class).getValue().doubleValue();
            final double deltaCargo = membraneContainer.getOuterPhaseConcentration(cargo).getValue().doubleValue() - membraneContainer.getInnerPhaseConcentration(cargo).getValue().doubleValue();
            value = -cargoConstant * deltaCargo * flux * membraneContainer.getInnerMembraneLayerConcentration(transporter).getValue().doubleValue();
        } else {
            value = 0.0;
        }
        return new Delta(membraneContainer.getInnerPhaseSection(), entity, Quantities.getQuantity(value, MOLE_PER_LITRE));
    }

    private boolean isCargo() {
        return getCurrentChemicalEntity().equals(cargo);
    }

    private boolean transporterInMembrane(MembraneContainer container) {
        return container.getOuterPhaseConcentration(cargo).getValue().doubleValue() != 0.0;
    }

    private boolean isOuterPhase(MembraneContainer container) {
        return getCurrentCellSection().equals(container.getOuterPhaseSection());
    }

    private boolean isInnerPhase(MembraneContainer container) {
        return getCurrentCellSection().equals(container.getInnerPhaseSection());
    }



}
