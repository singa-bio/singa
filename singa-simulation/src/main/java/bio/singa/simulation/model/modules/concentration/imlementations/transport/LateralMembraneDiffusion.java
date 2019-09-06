package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.diffusivity.MembraneDiffusivity;
import bio.singa.chemistry.features.diffusivity.SaffmanDelbrueckDiffusivityCorrelation;
import bio.singa.simulation.features.AffectedSection;
import bio.singa.simulation.features.Cargoes;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.EntityDeltaFunction;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.quantity.Quantities;

import java.util.*;

import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
public class LateralMembraneDiffusion extends ConcentrationBasedModule<EntityDeltaFunction> {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(LateralMembraneDiffusion.class);
    private static final MembraneDiffusivity DEFAULT_MEMBRANE_DIFFUSIVITY = SaffmanDelbrueckDiffusivityCorrelation.predict(Quantities.getQuantity(3.0, NANO(METRE)));
    private CellSubsection restrictedSubsection;

    public static EntityLimitationStep inSimulation(Simulation simulation) {
        return new LateralMembraneDiffusionBuilder(simulation);
    }

    public LateralMembraneDiffusion() {

    }

    private void postConstruct() {
        // apply
        setApplicationCondition(this::hasMembrane);
        // function
        AffectedSection affectedSection = getFeature(AffectedSection.class);
        EntityDeltaFunction function;
        if (affectedSection == null) {
            function = new EntityDeltaFunction(this::calculateDelta, this::unrestrictedApplication);
        } else {
            restrictedSubsection = affectedSection.getContent();
            function = new EntityDeltaFunction(this::calculateDelta, this::restrictedApplication);
        }
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

    private boolean unrestrictedApplication(ConcentrationContainer container) {
        return supplier.getCurrentSubsection().isMembrane();
    }

    private boolean restrictedApplication(ConcentrationContainer container) {
        return supplier.getCurrentSubsection().equals(restrictedSubsection);
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

    public interface EntityLimitationStep {
        EntityLimitationStep identifier(String identifier);

        BuildStep forEntity(ChemicalEntity chemicalEntity);

        BuildStep forAllEntities(ChemicalEntity... chemicalEntities);

        BuildStep forAllEntities(Collection<ChemicalEntity> chemicalEntities);

    }

    public interface SectionLimitationStep {

        BuildStep forMembrane(CellSubsection subsection);

        BuildStep forAllMembranes();

    }

    public interface BuildStep {
        LateralMembraneDiffusion build();
    }

    public static class LateralMembraneDiffusionBuilder implements EntityLimitationStep, SectionLimitationStep, BuildStep, ModuleBuilder<LateralMembraneDiffusion> {

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

        public BuildStep forEntity(ChemicalEntity chemicalEntity) {
            return forAllEntities(Collections.singletonList(chemicalEntity));
        }

        public BuildStep forAllEntities(ChemicalEntity... chemicalEntities) {
            return forAllEntities(Arrays.asList(chemicalEntities));
        }

        public BuildStep forAllEntities(Collection<ChemicalEntity> chemicalEntities) {
            for (ChemicalEntity chemicalEntity : chemicalEntities) {
                setDefaultFeatureIfNecessary(chemicalEntity);
            }
            module.setFeature(new Cargoes(new ArrayList<>(chemicalEntities)));
            return this;
        }

        private void setDefaultFeatureIfNecessary(ChemicalEntity entity) {
            if (entity.hasFeature(MembraneDiffusivity.class)) {
                return;
            }
            entity.setFeature(DEFAULT_MEMBRANE_DIFFUSIVITY);
        }

        @Override
        public BuildStep forMembrane(CellSubsection subsection) {
            if (!subsection.isMembrane()) {
                logger.warn("The supplied subsection {} is not annotated as a membrane.", subsection.getIdentifier());
            }
            module.setFeature(new AffectedSection(subsection));
            return this;
        }

        @Override
        public BuildStep forAllMembranes() {
            return this;
        }

        public LateralMembraneDiffusion build() {
            module.postConstruct();
            simulation.addModule(module);
            return module;
        }

    }

}
