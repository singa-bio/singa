package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourDependentModule;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.Collection;
import java.util.HashSet;
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

    private static Set<Class<? extends Feature>> requiredFeatures = new HashSet<>();

    static {
        requiredFeatures.add(Diffusivity.class);
    }

    public static SelectionStep inSimulation(Simulation simulation) {
        return new DiffusionBuilder(simulation);
    }

    private FreeDiffusion(Simulation simulation) {
        super(simulation);
        onlyApplyIf(updatable -> updatable instanceof  AutomatonNode);
        // apply everywhere
        addDeltaFunction(this::calculateDelta, this::onlyForReferencedEntities);
    }

    private void initialize() {
        addModuleToSimulation();
    }

    /**
     * Only apply, if current chemical entity is assigned in the referenced chemical entities.
     *
     * @param container
     * @return
     */
    private boolean onlyForReferencedEntities(ConcentrationContainer container) {
        return getReferencedEntities().contains(currentChemicalEntity);
    }

    private Delta calculateDelta(ConcentrationContainer concentrationContainer) {
        AutomatonNode currentNode = (AutomatonNode) getCurrentUpdatable();
        ChemicalEntity currentChemicalEntity = getCurrentChemicalEntity();
        CellSubsection currentCellSection = getCurrentCellSection();
        final double currentConcentration = concentrationContainer.get(currentCellSection, currentChemicalEntity).getValue().doubleValue();
        final double diffusivity = getScaledFeature(currentChemicalEntity, Diffusivity.class).getValue().doubleValue();
        // calculate entering term
        int numberOfNeighbors = 0;
        double concentration = 0;
        // traverse each neighbouring cells
        for (AutomatonNode neighbour : currentNode.getNeighbours()) {

            if (chemicalEntityIsNotMembraneAnchored() || bothAreNonMembrane(currentNode, neighbour) || bothAreMembrane(currentNode, neighbour)) {
                // if entity is not anchored in membrane
                // if current is membrane and neighbour is membrane
                // if current is non-membrane and neighbour is non-membrane
                // classical diffusion
                final Quantity<MolarConcentration> availableConcentration = neighbour.getConcentration(currentCellSection, currentChemicalEntity);
                if (availableConcentration != null) {
                    concentration += availableConcentration.getValue().doubleValue();
                    numberOfNeighbors++;
                }
            } else {
                // if current is non-membrane and neighbour is membrane
                if (neigbourIsPotentialSource(currentNode, neighbour)) {
                    // leaving amount stays unchanged, but entering concentration is relevant
                    final Quantity<MolarConcentration> availableConcentration = neighbour.getConcentration(currentCellSection, currentChemicalEntity);
                    if (availableConcentration != null) {
                        concentration += availableConcentration.getValue().doubleValue();
                    }
                }
                // if current is membrane and neighbour is non-membrane
                if (neigbourIsPotentialTarget(currentNode, neighbour)) {
                    // assert effect on leaving concentration but entering concentration stays unchanged
                    numberOfNeighbors++;
                }
            }

        }

        // entering amount
        final double enteringConcentration = concentration * diffusivity;
        // calculate leaving amount
        final double leavingConcentration = numberOfNeighbors * diffusivity * currentConcentration;
        // calculate next concentration
        final double delta = enteringConcentration - leavingConcentration;
        // return delta
        // System.out.println(delta);
        return new Delta(this, currentCellSection, currentChemicalEntity, Quantities.getQuantity(delta, Environment.getConcentrationUnit()));
    }

    private boolean chemicalEntityIsNotMembraneAnchored() {
        return !getCurrentChemicalEntity().isMembraneAnchored();
    }

    private boolean bothAreNonMembrane(AutomatonNode currentNode, AutomatonNode neighbour) {
        return !currentNode.getCellRegion().hasMembrane() && !neighbour.getCellRegion().hasMembrane();
    }

    private boolean bothAreMembrane(AutomatonNode currentNode, AutomatonNode neighbour) {
        return currentNode.getCellRegion().hasMembrane() && neighbour.getCellRegion().hasMembrane();
    }

    private boolean neigbourIsPotentialTarget(AutomatonNode currentNode, AutomatonNode neighbour) {
        return !currentNode.getCellRegion().hasMembrane() && neighbour.getCellRegion().hasMembrane();
    }

    private boolean neigbourIsPotentialSource(AutomatonNode currentNode, AutomatonNode neighbour) {
        return currentNode.getCellRegion().hasMembrane() && !neighbour.getCellRegion().hasMembrane();
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
        SelectionStep identifier(String identifier);

        BuildStep onlyFor(ChemicalEntity chemicalEntity);

        BuildStep forAll(ChemicalEntity... chemicalEntities);

        BuildStep forAll(Collection<ChemicalEntity> chemicalEntities);

    }

    public interface BuildStep {
        FreeDiffusion build();
    }

    public static class DiffusionBuilder implements SelectionStep, BuildStep {

        FreeDiffusion module;

        DiffusionBuilder(Simulation simulation) {
            module = new FreeDiffusion(simulation);
        }

        public DiffusionBuilder identifier(String identifier) {
            module.setIdentifier(new SimpleStringIdentifier(identifier));
            return this;
        }

        public BuildStep onlyFor(ChemicalEntity chemicalEntity) {
            module.addReferencedEntity(chemicalEntity);
            return this;
        }

        public BuildStep forAll(ChemicalEntity... chemicalEntities) {
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

        public FreeDiffusion build() {
            module.initialize();
            return module;
        }

    }

}