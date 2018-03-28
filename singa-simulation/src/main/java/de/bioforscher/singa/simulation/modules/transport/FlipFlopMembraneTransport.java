package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;

/**
 * @author cl
 */
public class FlipFlopMembraneTransport extends AbstractNeighbourIndependentModule {

    public FlipFlopMembraneTransport(Simulation simulation) {
        super(simulation);
        // apply this module only to membranes
        onlyApplyIf(node -> node.getState().equals(NodeState.MEMBRANE));
        // change of outer phase
        addDeltaFunction(this::calculateOuterPhaseDelta, this::onlyOuterPhase);
        // change of outer layer
        addDeltaFunction(this::calculateOuterLayerDelta, this::onlyOuterLayer);
        // change of inner layer
        addDeltaFunction(this::calculateInnerLayerDelta, this::onlyInnerLayer);
        // change of inner phase
        addDeltaFunction(this::calculateInnerPhaseDelta, this::onlyInnerPhase);
    }

    private boolean onlyOuterPhase(ConcentrationContainer concentrationContainer) {
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        return getCurrentCellSection().equals(membraneContainer.getOuterPhaseSection());
    }

    private Delta calculateOuterPhaseDelta(ConcentrationContainer concentrationContainer) {
        // resolve required parameters
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final Quantity<Frequency> kIn = getFeature(entity, MembraneEntry.class);
        final Quantity<Frequency> kOut = getFeature(entity, MembraneExit.class);
        // (outer phase) outer phase = -kIn * outer phase + kOut * outer layer
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        final double value = -kIn.getValue().doubleValue() * membraneContainer.getOuterPhaseConcentration(entity).getValue().doubleValue() +
                kOut.getValue().doubleValue() * membraneContainer.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
        return new Delta(this, membraneContainer.getOuterPhaseSection(), entity, Quantities.getQuantity(value, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

    private boolean onlyOuterLayer(ConcentrationContainer concentrationContainer) {
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        return getCurrentCellSection().equals(membraneContainer.getOuterLayerSection());
    }

    private Delta calculateOuterLayerDelta(ConcentrationContainer concentrationContainer) {
        // resolve required parameters
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final Quantity<Frequency> kIn = getFeature(entity, MembraneEntry.class);
        final Quantity<Frequency> kOut = getFeature(entity, MembraneExit.class);
        final Quantity<Frequency> kFlip = getFeature(entity, MembraneFlipFlop.class);
        // (outer layer) outer layer = kIn * outer phase - (kOut + kFlip) * outer layer + kFlip * inner layer
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        final double value = kIn.getValue().doubleValue() * membraneContainer.getOuterPhaseConcentration(entity).getValue().doubleValue() -
                (kOut.getValue().doubleValue() + kFlip.getValue().doubleValue()) * membraneContainer.getOuterMembraneLayerConcentration(entity).getValue().doubleValue() +
                kFlip.getValue().doubleValue() * membraneContainer.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
        return new Delta(this, membraneContainer.getOuterLayerSection(), entity, Quantities.getQuantity(value, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

    private boolean onlyInnerLayer(ConcentrationContainer concentrationContainer) {
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        return getCurrentCellSection().equals(membraneContainer.getInnerLayerSection());
    }

    private Delta calculateInnerLayerDelta(ConcentrationContainer concentrationContainer) {
        // resolve required parameters
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final Quantity<Frequency> kIn = getFeature(entity, MembraneEntry.class);
        final Quantity<Frequency> kOut = getFeature(entity, MembraneExit.class);
        final Quantity<Frequency> kFlip = getFeature(entity, MembraneFlipFlop.class);
        // (inner layer) inner layer = kIn * inner phase - (kOut + kFlip) * inner layer + kFlip * outer layer
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        final double value = kIn.getValue().doubleValue() * membraneContainer.getInnerPhaseConcentration(entity).getValue().doubleValue() -
                (kOut.getValue().doubleValue() + kFlip.getValue().doubleValue()) * membraneContainer.getInnerMembraneLayerConcentration(entity).getValue().doubleValue() +
                kFlip.getValue().doubleValue() * membraneContainer.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
        return new Delta(this, membraneContainer.getInnerLayerSection(), entity, Quantities.getQuantity(value, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

    private boolean onlyInnerPhase(ConcentrationContainer concentrationContainer) {
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        return getCurrentCellSection().equals(membraneContainer.getInnerPhaseSection());
    }

    private Delta calculateInnerPhaseDelta(ConcentrationContainer concentrationContainer) {
        // resolve required parameters
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final Quantity<Frequency> kIn = getFeature(entity, MembraneEntry.class);
        final Quantity<Frequency> kOut = getFeature(entity, MembraneExit.class);
        // (inner phase) inner phase = -kIn * inner phase + kOut * inner layer
        MembraneContainer membraneContainer = (MembraneContainer) concentrationContainer;
        final double value = -kIn.getValue().doubleValue() * membraneContainer.getInnerPhaseConcentration(getCurrentChemicalEntity()).getValue().doubleValue() +
                kOut.getValue().doubleValue() * membraneContainer.getInnerMembraneLayerConcentration(getCurrentChemicalEntity()).getValue().doubleValue();
        return new Delta(this, membraneContainer.getInnerPhaseSection(), getCurrentChemicalEntity(), Quantities.getQuantity(value, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

}
