package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourDependentModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.Set;

/**
 * Diffusion is the net movement of molecules or atoms from a region of high concentration to a region of low
 * concentration. This module defines the diffusion between {@link AutomatonNode}s in a {@link AutomatonGraph}, as
 * described by Fick's laws of diffusion.
 *
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Fick%27s_laws_of_diffusion">Wikipedia: Fick's laws of diffusion</a>
 */
public class FreeDiffusion extends AbstractNeighbourDependentModule {

    private Set<ChemicalEntity> chemicalEntities;

    public FreeDiffusion(Simulation simulation, Set<ChemicalEntity> chemicalEntities) {
        super(simulation);
        // apply everywhere
        this.chemicalEntities = chemicalEntities;
        addDeltaFunction(this::calculateDelta, this::onlyForReferencedEntities);
    }

    /**
     * Only apply, if current chemical entity is assigned in the referenced chemical entities.
     *
     * @param concentrationContainer
     * @return
     */
    private boolean onlyForReferencedEntities(ConcentrationContainer concentrationContainer) {
        return chemicalEntities.contains(currentChemicalEntity);
    }

    private Delta calculateDelta(ConcentrationContainer concentrationContainer) {
        ChemicalEntity currentChemicalEntity = getCurrentChemicalEntity();
        CellSection currentCellSection = getCurrentCellSection();
        final double currentConcentration = concentrationContainer.getAvailableConcentration(currentCellSection, currentChemicalEntity).getValue().doubleValue();
        // calculate entering term
        int numberOfNeighbors = 0;
        double concentration = 0;
        // traverse each neighbouring cells
        for (AutomatonNode neighbour : getCurrentNode().getNeighbours()) {
            final Quantity<MolarConcentration> availableConcentration = neighbour.getAvailableConcentration(currentChemicalEntity, currentCellSection);
            if (availableConcentration != null) {
                concentration += availableConcentration.getValue().doubleValue();
                numberOfNeighbors++;
            }
        }
        // entering amount
        final double enteringConcentration = concentration * getScaledFeature(currentChemicalEntity, Diffusivity.class).getValue().doubleValue();
        // calculate leaving amount
        final double leavingConcentration = numberOfNeighbors * getScaledFeature(currentChemicalEntity, Diffusivity.class).getValue().doubleValue() * currentConcentration;
        // calculate next concentration
        final double delta = enteringConcentration - leavingConcentration;
        // return delta
        return new Delta(this, currentCellSection, currentChemicalEntity, Quantities.getQuantity(delta, EnvironmentalParameters.getTransformedMolarConcentration()));

    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}