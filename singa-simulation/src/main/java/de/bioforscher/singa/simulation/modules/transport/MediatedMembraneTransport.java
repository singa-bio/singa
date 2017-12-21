package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Simulation;

/**
 * @author cl
 */
public class MediatedMembraneTransport extends AbstractNeighbourIndependentModule {

    public MediatedMembraneTransport(Simulation simulation) {
        super(simulation);
        // apply this module only to membranes
        onlyApplyIf(node -> node.getState().equals(NodeState.MEMBRANE));
        // change of outer phase
        addDeltaFunction(this::calculateOuterPhaseDelta, this::onlyOuterPhase);
        // change of inner phase
        addDeltaFunction(this::calculateInnerPhaseDelta, this::onlyInnerPhase);
    }

    private boolean onlyOuterPhase(ConcentrationContainer concentrationContainer) {
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        return getCurrentCellSection().equals(membraneContainer.getOuterPhaseSection());
    }

    private Delta calculateOuterPhaseDelta(ConcentrationContainer concentrationContainer) {
        // resolve required parameters

        // (outer phase) outer phase =
        return null;
    }

    private boolean onlyInnerPhase(ConcentrationContainer concentrationContainer) {
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        return getCurrentCellSection().equals(membraneContainer.getInnerPhaseSection());
    }

    private Delta calculateInnerPhaseDelta(ConcentrationContainer concentrationContainer) {
        // resolve required parameters

        // (inner phase) inner phase =

        return null;
    }

}
