package de.bioforscher.singa.simulation.modules.newmodules.imlementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import de.bioforscher.singa.simulation.modules.newmodules.functions.EntityDeltaFunction;
import de.bioforscher.singa.simulation.modules.newmodules.module.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.modules.newmodules.module.ModuleFactory;
import de.bioforscher.singa.simulation.modules.newmodules.simulation.Simulation;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.Collection;

/**
 * @author cl
 */
public class Diffusion extends ConcentrationBasedModule<EntityDeltaFunction> {

    public static SelectionStep inSimulation(Simulation simulation) {
        return new DiffusionBuilder(simulation);
    }

    public void initialize() {
        // apply
        setApplicationCondition(updatable -> updatable instanceof AutomatonNode);
        // function
        EntityDeltaFunction function = new EntityDeltaFunction(this::calculateDelta, this::onlyForReferencedEntities);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(Diffusivity.class);
        addModuleToSimulation();
    }

    private Delta calculateDelta(ConcentrationContainer concentrationContainer) {
        AutomatonNode node = (AutomatonNode) supplier.getCurrentUpdatable();
        ChemicalEntity entity = supplier.getCurrentEntity();
        CellSubsection subsection = supplier.getCurrentSubsection();
        final double currentConcentration = concentrationContainer.get(subsection, entity).getValue().doubleValue();
        final double diffusivity = getScaledFeature(entity, Diffusivity.class).getValue().doubleValue();
        // calculate entering term
        int numberOfNeighbors = 0;
        double concentration = 0;
        // traverse each neighbouring cells
        for (AutomatonNode neighbour : node.getNeighbours()) {

            if (chemicalEntityIsNotMembraneAnchored() || bothAreNonMembrane(node, neighbour) || bothAreMembrane(node, neighbour)) {
                // if entity is not anchored in membrane
                // if current is membrane and neighbour is membrane
                // if current is non-membrane and neighbour is non-membrane
                // classical diffusion
                final Quantity<MolarConcentration> availableConcentration = neighbour.getConcentration(subsection, entity);
                if (availableConcentration != null) {
                    concentration += availableConcentration.getValue().doubleValue();
                    numberOfNeighbors++;
                }
            } else {
                // if current is non-membrane and neighbour is membrane
                if (neigbourIsPotentialSource(node, neighbour)) {
                    // leaving amount stays unchanged, but entering concentration is relevant
                    final Quantity<MolarConcentration> availableConcentration = neighbour.getConcentration(subsection, entity);
                    if (availableConcentration != null) {
                        concentration += availableConcentration.getValue().doubleValue();
                    }
                }
                // if current is membrane and neighbour is non-membrane
                if (neigbourIsPotentialTarget(node, neighbour)) {
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
        return new Delta(this, subsection, entity, Quantities.getQuantity(delta, Environment.getConcentrationUnit()));
    }

    private boolean onlyForReferencedEntities(ConcentrationContainer container) {
        return getReferencedEntities().contains(supplier.getCurrentEntity());
    }

    private boolean chemicalEntityIsNotMembraneAnchored() {
        return !supplier.getCurrentEntity().isMembraneAnchored();
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

    public interface SelectionStep {
        SelectionStep identifier(String identifier);

        BuildStep onlyFor(ChemicalEntity chemicalEntity);

        BuildStep forAll(ChemicalEntity... chemicalEntities);

        BuildStep forAll(Collection<ChemicalEntity> chemicalEntities);

    }

    public interface BuildStep {
        Diffusion build();
    }

    public static class DiffusionBuilder implements SelectionStep, BuildStep {

        Diffusion module;

        DiffusionBuilder(Simulation simulation) {
            module = ModuleFactory.setupModule(Diffusion.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_DEPENDENT,
                    ModuleFactory.Specificity.ENTITY_SPECIFIC);
            module.setSimulation(simulation);
        }

        public DiffusionBuilder identifier(String identifier) {
            module.setIdentifier(identifier);
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

        public Diffusion build() {
            module.initialize();
            return module;
        }

    }

}
