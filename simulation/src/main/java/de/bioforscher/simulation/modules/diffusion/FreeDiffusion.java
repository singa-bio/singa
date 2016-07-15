package de.bioforscher.simulation.modules.diffusion;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.model.CumulativeUpdateBehavior;
import de.bioforscher.simulation.modules.model.Module;
import de.bioforscher.simulation.modules.model.PotentialUpdate;
import de.bioforscher.simulation.util.EnvironmentalVariables;
import de.bioforscher.units.UnitScaler;
import de.bioforscher.units.quantities.Diffusivity;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.units.UnitDictionary.MOLE_PER_LITRE;

/**
 * Diffusion is the net movement of molecules or atoms from a region of high concentration to a region of low
 * concentration. This module defines the diffusion between {@link BioNode}s in a {@link AutomatonGraph}, as descibed
 * by Fick's laws of diffusion.
 *
 * @author Christoph Leberecht
 * @see <a href="https://en.wikipedia.org/wiki/Fick%27s_laws_of_diffusion">Wikipedia: Fick's laws of diffusion</a>
 */
public class FreeDiffusion implements Module, CumulativeUpdateBehavior {

    private Map<ChemicalEntity, Quantity<Diffusivity>> diffusionCoefficients;

    public FreeDiffusion() {
        this.diffusionCoefficients = new HashMap<>();
    }

    public void prepareDiffusionCoefficients(Set<ChemicalEntity> entities) {
        for (ChemicalEntity species : entities) {
            Quantity<Diffusivity> diffusionCoefficient = determineDiffusionCoefficient(species);
            this.diffusionCoefficients.put(species, diffusionCoefficient);
        }
    }

    @Override
    public void applyTo(AutomatonGraph graph) {
        updateGraph(graph);
    }

    @Override
    public PotentialUpdate calculateUpdate(BioNode node, ChemicalEntity entity) {
        // get coefficient
        Quantity<Diffusivity> coefficient = getDiffusionCoefficient(entity);
        // sum up concentrations
        double neighbourConcentration = node.getNeighbours().stream()
                .mapToDouble(neighbor -> neighbor.getConcentration(entity).getValue().doubleValue())
                .sum();
        // get number of neighbors
        int neighbours = node.getNeighbours().size();
        // current concentration of this node
        double currentConcentration = node.getConcentration(entity).getValue().doubleValue();
        // calculate next concentration
        double nextConcentration = coefficient.getValue().doubleValue() * neighbourConcentration + (1 -
                neighbours * coefficient.getValue().doubleValue()) * currentConcentration;
        return new PotentialUpdate(entity, Quantities.getQuantity(nextConcentration, MOLE_PER_LITRE));
    }

    /**
     * Determines the diffusion coefficient if it is not already cached.
     *
     * @param entity The entity.
     * @return
     */
    private Quantity<Diffusivity> getDiffusionCoefficient(ChemicalEntity entity) {
        if (this.diffusionCoefficients.containsKey(entity)) {
            return this.diffusionCoefficients.get(entity);
        } else {
            Quantity<Diffusivity> coefficient = determineDiffusionCoefficient(entity);
            this.diffusionCoefficients.put(entity, coefficient);
            return coefficient;
        }
    }

    /**
     * Determines the diffusivity of the entity and scales it to the dimensions of the system.
     *
     * @param entity The entity.
     * @return The diffusivity of the entity.
     */
    private Quantity<Diffusivity> determineDiffusionCoefficient(ChemicalEntity entity) {
        Quantity<Diffusivity> diffusivityApproximation = DiffusionUtilities.estimateDiffusivity(entity);
        return scaleDiffusivity(diffusivityApproximation);
    }

    /**
     * Scales the given diffusivity to the dimensions and features of the system.
     *
     * @param diffusivity The diffusivity to be scaled.
     * @return The scaled diffusivity.
     */
    private Quantity<Diffusivity> scaleDiffusivity(Quantity<Diffusivity> diffusivity) {
        Quantity<Diffusivity> correlatedDiffusivity = UnitScaler.rescaleDiffusivity(diffusivity,
                EnvironmentalVariables.getInstance().getTimeStep(),
                EnvironmentalVariables.getInstance().getNodeDistance());
        // artificially slow if this is a cellular environment
        if (EnvironmentalVariables.getInstance().isCellularEnvironment()) {
            correlatedDiffusivity = correlatedDiffusivity.multiply(DiffusionUtilities.STDF_CELL_WATER.getValue());
        }
        return correlatedDiffusivity;
    }

}