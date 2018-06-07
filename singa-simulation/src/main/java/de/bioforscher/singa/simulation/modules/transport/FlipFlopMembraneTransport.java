package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.simulation.model.newsections.CellTopology.*;

/**
 * @author cl
 */
public class FlipFlopMembraneTransport extends AbstractNeighbourIndependentModule {

    public static SelectionStep inSimulation(Simulation simulation) {
        return new FlipFlopMembraneTransportBuilder(simulation);
    }

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();
    static {
        requiredFeatures.add(MembraneEntry.class);
        requiredFeatures.add(MembraneExit.class);
        requiredFeatures.add(MembraneFlipFlop.class);
    }

    public FlipFlopMembraneTransport(Simulation simulation) {
        super(simulation);
        // apply this module to membranes and vesicles

        // change of outer phase
        addDeltaFunction(this::calculateOuterPhaseDelta, this::onlyOuterPhase);
        // change of outer layer
        addDeltaFunction(this::calculateOuterLayerDelta, this::onlyOuterLayer);
        // change of inner layer
        addDeltaFunction(this::calculateInnerLayerDelta, this::onlyInnerLayer);
        // change of inner phase
        addDeltaFunction(this::calculateInnerPhaseDelta, this::onlyInnerPhase);
    }

    private void initialize() {
        addModuleToSimulation();
    }

    private boolean onlyOuterPhase(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getOuterSubsection().equals(getCurrentCellSection()) && onlyForReferencedEntities();
    }

    private Delta calculateOuterPhaseDelta(ConcentrationContainer container) {
        // resolve required parameters
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final Quantity<Frequency> kIn = getScaledFeature(entity, MembraneEntry.class);
        final Quantity<Frequency> kOut = getScaledFeature(entity, MembraneExit.class);
        // (outer phase) outer phase = -kIn * outer phase + kOut * outer layer
        final double value = -kIn.getValue().doubleValue() * container.get(OUTER, entity).getValue().doubleValue() +
                kOut.getValue().doubleValue() * container.get(MEMBRANE_OUTER_LAYER, entity).getValue().doubleValue();
        // return new Delta(this, container.getOuterSubsection(), entity, Quantities.getQuantity(value, Environment.getConcentrationUnit()));
        return null;
    }

    private boolean onlyOuterLayer(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getSubsection(MEMBRANE_OUTER_LAYER).equals(getCurrentCellSection()) && onlyForReferencedEntities();
    }

    private Delta calculateOuterLayerDelta(ConcentrationContainer container) {
        // resolve required parameters
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final Quantity<Frequency> kIn = getScaledFeature(entity, MembraneEntry.class);
        final Quantity<Frequency> kOut = getScaledFeature(entity, MembraneExit.class);
        final Quantity<Frequency> kFlip = getScaledFeature(entity, MembraneFlipFlop.class);
        // (outer layer) outer layer = kIn * outer phase - (kOut + kFlip) * outer layer + kFlip * inner layer
        final double value = kIn.getValue().doubleValue() * container.get(OUTER, entity).getValue().doubleValue() -
                (kOut.getValue().doubleValue() + kFlip.getValue().doubleValue()) * container.get(MEMBRANE_OUTER_LAYER, entity).getValue().doubleValue() +
                kFlip.getValue().doubleValue() * container.get(MEMBRANE_INNER_LAYER, entity).getValue().doubleValue();
        // return new Delta(this, container.getSubsection(MEMBRANE_OUTER_LAYER), entity, Quantities.getQuantity(value, Environment.getConcentrationUnit()));
        return null;
    }

    private boolean onlyInnerLayer(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getSubsection(MEMBRANE_INNER_LAYER).equals(getCurrentCellSection()) && onlyForReferencedEntities();
    }

    private Delta calculateInnerLayerDelta(ConcentrationContainer container) {
        // resolve required parameters
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final Quantity<Frequency> kIn = getScaledFeature(entity, MembraneEntry.class);
        final Quantity<Frequency> kOut = getScaledFeature(entity, MembraneExit.class);
        final Quantity<Frequency> kFlip = getScaledFeature(entity, MembraneFlipFlop.class);
        // (inner layer) inner layer = kIn * inner phase - (kOut + kFlip) * inner layer + kFlip * outer layer
        final double value = kIn.getValue().doubleValue() * container.get(INNER, entity).getValue().doubleValue() -
                (kOut.getValue().doubleValue() + kFlip.getValue().doubleValue()) * container.get(MEMBRANE_INNER_LAYER, entity).getValue().doubleValue() +
                kFlip.getValue().doubleValue() * container.get(MEMBRANE_OUTER_LAYER, entity).getValue().doubleValue();
        // return new Delta(this, container.getSubsection(MEMBRANE_INNER_LAYER), entity, Quantities.getQuantity(value, Environment.getConcentrationUnit()));
        return null;
    }

    private boolean onlyInnerPhase(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getInnerSubsection().equals(getCurrentCellSection()) && onlyForReferencedEntities();
    }

    private Delta calculateInnerPhaseDelta(ConcentrationContainer container) {
        // resolve required parameters
        final ChemicalEntity entity = getCurrentChemicalEntity();
        final Quantity<Frequency> kIn = getScaledFeature(entity, MembraneEntry.class);
        final Quantity<Frequency> kOut = getScaledFeature(entity, MembraneExit.class);
        // (inner phase) inner phase = -kIn * inner phase + kOut * inner layer
        final double value = -kIn.getValue().doubleValue() * container.get(INNER, entity).getValue().doubleValue() +
                kOut.getValue().doubleValue() * container.get(MEMBRANE_INNER_LAYER, entity).getValue().doubleValue();
        // return new Delta(this, container.getInnerSubsection(), entity, Quantities.getQuantity(value, Environment.getConcentrationUnit()));
        return null;
    }

    /**
     * Only apply, if current chemical entity is assigned in the referenced chemical entities.
     *
     * @return True, if current chemical entity is assigned in the referenced chemical entities.
     */
    private boolean onlyForReferencedEntities() {
        return getReferencedEntities().contains(getCurrentChemicalEntity());
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return requiredFeatures;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public interface SelectionStep {
        BuildStep onlyFor(ChemicalEntity chemicalEntity);

        BuildStep forAll(ChemicalEntity ... chemicalEntities );

        BuildStep forAll(Collection<ChemicalEntity> chemicalEntities);

    }

    public interface BuildStep {
        FlipFlopMembraneTransport build();
    }

    public static class FlipFlopMembraneTransportBuilder implements SelectionStep, BuildStep {

        FlipFlopMembraneTransport module;

        public FlipFlopMembraneTransportBuilder(Simulation simulation) {
            module = new FlipFlopMembraneTransport(simulation);
        }

        public BuildStep onlyFor(ChemicalEntity chemicalEntity) {
            module.addReferencedEntity(chemicalEntity);
            return this;
        }

        public BuildStep forAll(ChemicalEntity ... chemicalEntities ) {
            for (ChemicalEntity chemicalEntity : chemicalEntities) {
                module.addReferencedEntity(chemicalEntity);
            }
            return this;
        }

        public BuildStep forAll(Collection<ChemicalEntity> chemicalEntities) {
            for (ChemicalEntity chemicalEntity : chemicalEntities) {
                module.addReferencedEntity(chemicalEntity);
            }
            return this;
        }

        public FlipFlopMembraneTransport build() {
            module.initialize();
            return module;
        }

    }

}
