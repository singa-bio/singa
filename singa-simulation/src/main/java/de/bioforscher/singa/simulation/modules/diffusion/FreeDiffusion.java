package de.bioforscher.singa.simulation.modules.diffusion;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.updates.CumulativeUpdateBehavior;
import de.bioforscher.singa.simulation.modules.model.updates.PotentialUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * Diffusion is the net movement of molecules or atoms from a region of high concentration to a region of low
 * concentration. This module defines the diffusion between {@link BioNode}s in a {@link AutomatonGraph}, as described
 * by Fick's laws of diffusion.
 *
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Fick%27s_laws_of_diffusion">Wikipedia: Fick's laws of diffusion</a>
 */
public class FreeDiffusion implements Module, CumulativeUpdateBehavior {

    private static final Logger logger = LoggerFactory.getLogger(FreeDiffusion.class);

    private Set<ChemicalEntity<?>> chemicalEntities;

    public FreeDiffusion() {
        this.chemicalEntities = new HashSet<>();
    }

    public void prepareDiffusionCoefficients(Set<ChemicalEntity<?>> entities) {
        this.chemicalEntities = entities;
        for (ChemicalEntity entity : entities) {
            // determine diffusion coefficients
            setUpDiffusivity(entity);
        }
    }

    @Override
    public void applyTo(AutomatonGraph graph) {
        updateGraph(graph);
    }

    @Override
    public List<PotentialUpdate> calculateUpdates(BioNode node) {
        List<PotentialUpdate> updates = new ArrayList<>();
        for (CellSection section : node.getAllReferencedSections()) {
            for (ChemicalEntity entity : node.getAllReferencedEntities()) {
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
        final double enteringConcentration = concentration * getDiffusivity(entity).getValue().doubleValue();
        // calculate leaving amount
        final double leavingConcentration = numberOfNeighbors * getDiffusivity(entity).getValue().doubleValue() * currentConcentration;
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
    private Quantity<Diffusivity> getDiffusivity(ChemicalEntity<?> entity) {
        if (!this.chemicalEntities.contains(entity)) {
            this.chemicalEntities.add(entity);
            setUpDiffusivity(entity);
        }
        return entity.getFeature(Diffusivity.class).getScaledQuantity();
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.chemicalEntities;
    }

    /**
     * Determines the diffusivity of the entity and scales it to the dimensions of the system.
     *
     * @param entity The entity.
     * @return The diffusivity of the entity.
     */
    private void setUpDiffusivity(ChemicalEntity<?> entity) {
        entity.setFeature(Diffusivity.class);
        Diffusivity feature = entity.getFeature(Diffusivity.class);
        feature.scale(EnvironmentalParameters.getInstance().getTimeStep(),
                EnvironmentalParameters.getInstance().getNodeDistance());
    }

    public Quantity<Diffusivity> getMaximalDiffusivity() {
        Quantity<Diffusivity> max = Quantities.getQuantity(0.0, SQUARE_CENTIMETER_PER_SECOND);
        for (ChemicalEntity<?> chemicalEntity : this.chemicalEntities) {
            Quantity<Diffusivity> current = chemicalEntity.getFeature(Diffusivity.class).getScaledQuantity();
            if (current.getValue().doubleValue() > max.getValue().doubleValue()) {
                max = current;
            }
        }
        return max;
    }

    public ChemicalEntity getEntityWithMaximalDiffusivity() {
        final Quantity<Diffusivity> maximalDiffusivity = getMaximalDiffusivity();
        return this.chemicalEntities.stream()
                .filter(entity -> entity.getFeature(Diffusivity.class).getScaledQuantity().getValue().doubleValue() == maximalDiffusivity.getValue().doubleValue())
                .findFirst().orElse(null);
    }

    public void fixDiffusionCoefficientForEntity(ChemicalEntity entity, Quantity<Diffusivity> diffusivityQuantity) {
        Diffusivity diffusivity = new Diffusivity(diffusivityQuantity, FeatureOrigin.MANUALLY_ANNOTATED);
        diffusivity.scale(EnvironmentalParameters.getInstance().getTimeStep(),
                EnvironmentalParameters.getInstance().getNodeDistance());
        entity.setFeature(diffusivity);
        this.chemicalEntities.add(entity);
    }

}