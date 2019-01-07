package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureProvider;
import bio.singa.simulation.features.Cargoes;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.EntityDeltaFunction;
import bio.singa.simulation.model.modules.concentration.scope.DependentUpdate;
import bio.singa.simulation.model.modules.concentration.specifity.EntitySpecific;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Diffusion is the fundamental force governing the random movement of molecules in cells. As a
 * {@link ConcentrationBasedModule} it has the {@link DependentUpdate} scope and is {@link EntitySpecific}. The module
 * is only applied for automaton nodes for the entities specified during the build process (via
 * {@link DiffusionBuilder}). Diffusion is parametrized by the {@link Diffusivity} {@link Feature}, therefore
 * Diffusivity must be assigned to each entity or a {@link FeatureProvider} will try to resolve it.
 * Entities might be anchored to membranes ({@link ChemicalEntity#setMembraneAnchored(boolean)}), which permits
 * diffusion to nodes with non membrane regions.
 * <pre>
 *  // define the feature to parametrize the diffusion
 *  Diffusivity diffusivity = new Diffusivity(Quantities.getValue(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND),
 *         Evidence.MANUALLY_ANNOTATED);
 *  // assign it to the chemical entity
 *  SmallMolecule ammonia = new SmallMolecule.Builder("ammonia")
 *         .name("ammonia")
 *         .assignFeature(diffusivity)
 *         .build();
 *  // create the module
 *  Diffusion diffusion = Diffusion.inSimulation(simulation)
 *         .identifier("ammonia diffusion")
 *         .onlyFor(ammonia)
 *         .build(); </pre>
 *
 * @author cl
 */
public class Diffusion extends ConcentrationBasedModule<EntityDeltaFunction> {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Diffusion.class);

    public static SelectionStep inSimulation(Simulation simulation) {
        return new DiffusionBuilder(simulation);
    }

    public Diffusion() {

    }

    private void postConstruct() {
        // apply
        setApplicationCondition(updatable -> updatable instanceof AutomatonNode);
        // function
        EntityDeltaFunction function = new EntityDeltaFunction(this::calculateDelta, this::onlyForReferencedEntities);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(Diffusivity.class);
        List<ChemicalEntity> cargoes = getFeature(Cargoes.class).getContent();
        addReferencedEntities(cargoes);
    }

    private ConcentrationDelta calculateDelta(ConcentrationContainer concentrationContainer) {
        AutomatonNode node = (AutomatonNode) supplier.getCurrentUpdatable();
        ChemicalEntity entity = supplier.getCurrentEntity();
        CellSubsection subsection = supplier.getCurrentSubsection();
        final double currentConcentration = concentrationContainer.get(subsection, entity);
        final double diffusivity = getScaledFeature(entity, Diffusivity.class);
        // calculate entering term
        int numberOfNeighbors = 0;
        double concentration = 0;
        // traverse each neighbouring cells
        for (AutomatonNode neighbour : node.getNeighbours()) {

            if (neighbour.getConcentrationContainer().getReferencedSubSections().contains(subsection)) {
                // if the neighbour actually contains the same subsection, that is currently handled
                if (chemicalEntityIsNotMembraneAnchored() || bothAreNonMembrane(node, neighbour) || bothAreMembrane(node, neighbour)) {
                    // if entity is not anchored in membrane
                    // if current is membrane and neighbour is membrane
                    // if current is non-membrane and neighbour is non-membrane
                    // classical diffusion
                    // if the neighbour actually contains the same subsection
                    double availableConcentration = neighbour.getConcentrationContainer().get(subsection, entity);
                    concentration += availableConcentration;
                    numberOfNeighbors++;

                } else {
                    // if current is non-membrane and neighbour is membrane
                    if (neighborIsPotentialSource(node, neighbour)) {
                        // leaving amount stays unchanged, but entering concentration is relevant
                        double availableConcentration = neighbour.getConcentrationContainer().get(subsection, entity);
                        concentration += availableConcentration;
                    }
                    // if current is membrane and neighbour is non-membrane
                    if (neighborIsPotentialTarget(node, neighbour)) {
                        // assert effect on leaving concentration but entering concentration stays unchanged
                        numberOfNeighbors++;
                    }
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
        return new ConcentrationDelta(this, subsection, entity, delta);
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

    private boolean neighborIsPotentialTarget(AutomatonNode currentNode, AutomatonNode neighbour) {
        return !currentNode.getCellRegion().hasMembrane() && neighbour.getCellRegion().hasMembrane();
    }

    private boolean neighborIsPotentialSource(AutomatonNode currentNode, AutomatonNode neighbour) {
        return currentNode.getCellRegion().hasMembrane() && !neighbour.getCellRegion().hasMembrane();
    }

    @Override
    public void checkFeatures() {
        logger.debug("The module " + getClass().getSimpleName() + " requires the Feature Diffusivity to be annotated to all requested chemical entities.");
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new DiffusionBuilder(simulation);
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

    public static class DiffusionBuilder implements SelectionStep, BuildStep, ModuleBuilder<Diffusion> {

        Diffusion module;
        private Simulation simulation;

        public DiffusionBuilder(Simulation simulation) {
            this.simulation = simulation;
            createModule(simulation);
        }

        @Override
        public Diffusion getModule() {
            return module;
        }


        @Override
        public Diffusion createModule(Simulation simulation) {
            module = ModuleFactory.setupModule(Diffusion.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_DEPENDENT,
                    ModuleFactory.Specificity.ENTITY_SPECIFIC);
            return module;
        }

        public DiffusionBuilder identifier(String identifier) {
            module.setIdentifier(identifier);
            return this;
        }

        public BuildStep onlyFor(ChemicalEntity chemicalEntity) {
            module.setFeature(new Cargoes(Collections.singletonList(chemicalEntity), Evidence.NO_EVIDENCE));
            return this;
        }

        public BuildStep forAll(ChemicalEntity... chemicalEntities) {
            module.setFeature(new Cargoes(Arrays.asList(chemicalEntities), Evidence.NO_EVIDENCE));
            return this;
        }

        public BuildStep forAll(Collection<ChemicalEntity> chemicalEntities) {
            module.setFeature(new Cargoes(new ArrayList<>(chemicalEntities), Evidence.NO_EVIDENCE));
            return this;
        }

        public Diffusion build() {
            module.postConstruct();
            simulation.addModule(module);
            return module;
        }

    }

}
