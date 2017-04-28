package de.bioforscher.singa.simulation.modules.diffusion;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.updates.CumulativeUpdateBehavior;
import de.bioforscher.singa.simulation.modules.model.updates.PotentialUpdate;
import de.bioforscher.singa.units.UnitScaler;
import de.bioforscher.singa.units.quantities.Diffusivity;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.*;

import static de.bioforscher.singa.units.UnitProvider.MOLE_PER_LITRE;

/**
 * Diffusion is the net movement of molecules or atoms from a region of high concentration to a region of low
 * concentration. This module defines the diffusion between {@link BioNode}s in a {@link AutomatonGraph}, as described
 * by Fick's laws of diffusion.
 *
 * @author Christoph Leberecht
 * @see <a href="https://en.wikipedia.org/wiki/Fick%27s_laws_of_diffusion">Wikipedia: Fick's laws of diffusion</a>
 */
public class FreeDiffusion implements Module, CumulativeUpdateBehavior {

    private Map<ChemicalEntity<?>, Quantity<Diffusivity>> diffusionCoefficients;


    public FreeDiffusion() {
        this.diffusionCoefficients = new HashMap<>();

    }

    public void prepareDiffusionCoefficients(Set<ChemicalEntity<?>> entities) {
        for (ChemicalEntity entity : entities) {
            // determine diffusion coefficients
            Quantity<Diffusivity> diffusionCoefficient = determineDiffusionCoefficient(entity);
            this.diffusionCoefficients.put(entity, diffusionCoefficient);
        }
    }

    @Override
    public void applyTo(AutomatonGraph graph) {
        updateGraph(graph);
    }

    @Override
    public List<PotentialUpdate> calculateUpdates(BioNode node) {
        List<PotentialUpdate> updates = new ArrayList<>();
        for (CellSection section: node.getAllReferencedSections()) {
            for (ChemicalEntity entity: node.getAllReferencedEntities()) {
                updates.add(calculateCompartmentSpecificUpdate(node, section, entity));
            }
        }
        return updates;
    }

    private PotentialUpdate calculateCompartmentSpecificUpdate(BioNode node, CellSection cellSection, ChemicalEntity entity) {
        final double currentConcentration = node.getAvailableConcentration(entity, cellSection).getValue().doubleValue();
        // calculate entering term
        int numberOfNeighbors = 0;
        double concentration = 0;
        // traverse each neighbouring cell
        for (BioNode neighbour : node.getNeighbours()) {
            numberOfNeighbors++;
            // if the node is from an different compartment
            concentration += neighbour.getAvailableConcentration(entity, cellSection).getValue().doubleValue();
        }
        // entering amount
        final double enteringConcentration = concentration * getDiffusionCoefficient(entity).getValue().doubleValue();
        // calculate leaving amount
        final double leavingConcentration = numberOfNeighbors * getDiffusionCoefficient(entity).getValue().doubleValue() * currentConcentration;
        // calculate next concentration
        final double nextConcentration = enteringConcentration - leavingConcentration + currentConcentration;
        return new PotentialUpdate(node, cellSection, entity, Quantities.getQuantity(nextConcentration, MOLE_PER_LITRE));
    }


    /**
     * Determines the diffusion coefficient if it is not already cached.
     *
     * @param entity The entity.
     * @return The Diffusion coefficient.
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

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.diffusionCoefficients.keySet();
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
                EnvironmentalParameters.getInstance().getTimeStep(),
                EnvironmentalParameters.getInstance().getNodeDistance());
        // artificially slow if this is a cellular environment
        if (EnvironmentalParameters.getInstance().isCellularEnvironment()) {
            correlatedDiffusivity = correlatedDiffusivity.multiply(DiffusionUtilities.STDF_CELL_WATER.getValue());
        }
        return correlatedDiffusivity;
    }

    public void fixDiffusionCoefficientForEntity(ChemicalEntity entity, Quantity<Diffusivity> diffusivity) {
        this.diffusionCoefficients.put(entity, scaleDiffusivity(diffusivity));
    }

    public Quantity<Diffusivity> getMaximalDiffusivity() {
        // FIXME this is not good
        return Quantities.getQuantity(this.diffusionCoefficients.values().stream()
                .mapToDouble(diffusivity -> diffusivity.getValue().doubleValue())
                .max().orElse(0.0), this.diffusionCoefficients.get(this.diffusionCoefficients.keySet().iterator().next
                ()).getUnit());
    }

    public ChemicalEntity getEntityWithMaximalDiffusivity() {
        Quantity<Diffusivity> maximalDiffusivity = getMaximalDiffusivity();
        return this.diffusionCoefficients.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), maximalDiffusivity))
                .map(Map.Entry::getKey)
                .findFirst().get();

    }

}