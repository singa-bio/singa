package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.diffusivity.MembraneDiffusivity;
import bio.singa.features.model.Evidence;
import bio.singa.simulation.features.Cargoes;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.EntityDeltaFunction;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;

/**
 * @author cl
 */
public class LateralMembraneDiffusion extends ConcentrationBasedModule<EntityDeltaFunction> {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Diffusion.class);

    public static SelectionStep inSimulation(Simulation simulation) {
        return new LateralMembraneDiffusionBuilder(simulation);
    }

    public LateralMembraneDiffusion() {

    }

    private void postConstruct() {
        // apply
        setApplicationCondition(this::hasMembrane);
        // function
        EntityDeltaFunction function = new EntityDeltaFunction(this::calculateDelta, this::applicationCondition);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(MembraneDiffusivity.class);
        List<ChemicalEntity> cargoes = getFeature(Cargoes.class).getContent();
        addReferencedEntities(cargoes);
    }

    private boolean hasMembrane(Updatable updatable) {
        if (!(updatable instanceof AutomatonNode)) {
            return false;
        }
        AutomatonNode node = (AutomatonNode) updatable;
        return node.getCellRegion().hasMembrane();
    }

    private boolean applicationCondition(ConcentrationContainer container) {
        return supplier.getCurrentSubsection().isMembrane();
    }

    private ConcentrationDelta calculateDelta(ConcentrationContainer concentrationContainer) {
        AutomatonNode node = (AutomatonNode) supplier.getCurrentUpdatable();
        ChemicalEntity entity = supplier.getCurrentEntity();
        final double currentConcentration = concentrationContainer.get(MEMBRANE, entity);
        final double diffusivity = getScaledFeature(entity, MembraneDiffusivity.class);
        // traverse each neighbouring subsection
        double delta = 0.0;
        for (AutomatonNode neighbour : node.getNeighbours()) {
            if (neighbour.getCellRegion().hasMembrane()) {
                delta += diffusivity * (neighbour.getConcentrationContainer().get(MEMBRANE, entity) - currentConcentration);
            }
        }
        // return delta
        return new ConcentrationDelta(this, supplier.getCurrentSubsection(), entity, delta);
    }

    @Override
    public void checkFeatures() {
        logger.debug("The module " + getClass().getSimpleName() + " requires the Feature MembraneDiffusivity to be annotated to all requested chemical entities.");
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new LateralMembraneDiffusionBuilder(simulation);
    }

    public interface SelectionStep {
        SelectionStep identifier(String identifier);

        BuildStep onlyFor(ChemicalEntity chemicalEntity);

        BuildStep forAll(ChemicalEntity... chemicalEntities);

        BuildStep forAll(Collection<ChemicalEntity> chemicalEntities);

    }

    public interface BuildStep {
        LateralMembraneDiffusion build();
    }

    public static class LateralMembraneDiffusionBuilder implements SelectionStep, BuildStep, ModuleBuilder<LateralMembraneDiffusion> {

        LateralMembraneDiffusion module;
        private Simulation simulation;

        public LateralMembraneDiffusionBuilder(Simulation simulation) {
            this.simulation = simulation;
            createModule(simulation);
        }

        @Override
        public LateralMembraneDiffusion getModule() {
            return module;
        }


        @Override
        public LateralMembraneDiffusion createModule(Simulation simulation) {
            module = ModuleFactory.setupModule(LateralMembraneDiffusion.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_DEPENDENT,
                    ModuleFactory.Specificity.ENTITY_SPECIFIC);
            return module;
        }

        public LateralMembraneDiffusionBuilder identifier(String identifier) {
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

        public LateralMembraneDiffusion build() {
            module.postConstruct();
            simulation.addModule(module);
            return module;
        }

    }

}
