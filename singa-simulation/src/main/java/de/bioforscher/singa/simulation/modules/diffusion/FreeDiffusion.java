package de.bioforscher.singa.simulation.modules.diffusion;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.AbstractModule;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.Map;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * Diffusion is the net movement of molecules or atoms from a region of high concentration to a region of low
 * concentration. This module defines the diffusion between {@link BioNode}s in a {@link AutomatonGraph}, as described
 * by Fick's laws of diffusion.
 *
 * @author Christoph Leberecht
 * @see <a href="https://en.wikipedia.org/wiki/Fick%27s_laws_of_diffusion">Wikipedia: Fick's laws of diffusion</a>
 */
public class FreeDiffusion extends AbstractModule {

    public FreeDiffusion(Simulation simulation) {
        super(simulation);
        // apply to every node
        applyAlways();
        // change
        addDeltaFunction(this::calculateDelta);

    }

    private Delta calculateDelta(ConcentrationContainer concentrationContainer) {
        BioNode currentNode = getCurrentNode();
        ChemicalEntity currentChemicalEntity = getCurrentChemicalEntity();
        CellSection currentCellSection = getCurrentCellSection();
        final double currentConcentration = concentrationContainer.getAvailableConcentration(currentCellSection, currentChemicalEntity).getValue().doubleValue();

        // FIXME the module also needs to know the half concentrations at each step, it would be best to calculate the
        // FIXME full deltas for all nodes, than calculate based on that the half concentrations, than the half deltas
        // FIXME and errors

        // calculate entering term
        int numberOfNeighbors = 0;
        double concentration = 0;
        // traverse each neighbouring cells
        for (BioNode neighbour : currentNode.getNeighbours()) {
            Map<ChemicalEntity, Quantity<MolarConcentration>> concentrations = neighbour.getAllConcentrationsForSection(cellSection);
            if (!concentrations.isEmpty()) {
                numberOfNeighbors++;
                concentration += concentrations.get(entity).getValue().doubleValue();
            }
        }
        // entering amount
        final double enteringConcentration = concentration * getDiffusivity(entity).getValue().doubleValue();
        // calculate leaving amount
        final double leavingConcentration = numberOfNeighbors * getDiffusivity(entity).getValue().doubleValue() * currentConcentration;
        // calculate next concentration
        final double delta = enteringConcentration - leavingConcentration; //+ currentConcentration;
        // add delta to node
        node.addDelta(new Delta(cellSection, entity, Quantities.getQuantity(delta, MOLE_PER_LITRE)));
    }





}