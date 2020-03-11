package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.diffusivity.MembraneDiffusivity;
import bio.singa.chemistry.features.diffusivity.SaffmanDelbrueckDiffusivityCorrelation;
import bio.singa.simulation.features.AffectedRegion;
import bio.singa.simulation.features.Cargoes;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleBuilder;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.EntityDeltaFunction;
import bio.singa.simulation.model.sections.CellRegion;
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
    private CellRegion restrictedRegion;

    public LateralMembraneDiffusion() {

    }

    public static EntityLimitationStep inSimulation(Simulation simulation) {
        return new LateralMembraneDiffusionBuilder(simulation);
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new LateralMembraneDiffusionBuilder(simulation);
    }

    private void postConstruct() {
        // apply
        setApplicationCondition(this::hasMembrane);
        // function
        AffectedRegion affectedRegion = getFeature(AffectedRegion.class);
        EntityDeltaFunction function;
        if (affectedRegion == null) {
            function = new EntityDeltaFunction(this::calculateDelta, this::unrestrictedApplication);
        } else {
            restrictedRegion = affectedRegion.getContent();
            function = new EntityDeltaFunction(this::calculateDelta, this::restrictedApplication);
        }
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(MembraneDiffusivity.class);
        List<ChemicalEntity> cargoes = getFeature(Cargoes.class).getContent();
        for (ChemicalEntity cargo : cargoes) {
            LateralMembraneDiffusionBuilder.setDefaultFeatureIfNecessary(cargo);
        }
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
        return supplier.getCurrentUpdatable().getCellRegion().equals(restrictedRegion) && supplier.getCurrentSubsection().isMembrane();
    }

    private ConcentrationDelta calculateDelta(ConcentrationContainer concentrationContainer) {
        AutomatonNode node = (AutomatonNode) supplier.getCurrentUpdatable();
        ChemicalEntity entity = supplier.getCurrentEntity();
        final double currentConcentration = concentrationContainer.get(MEMBRANE, entity);
        final double diffusivity = getScaledFeature(entity, MembraneDiffusivity.class);
        // traverse each neighbouring subsection
        double delta = 0.0;
        for (AutomatonNode neighbour : node.getNeighbours()) {
            if (!neighbour.getCellRegion().equals(restrictedRegion)) {
                continue;
            }
            delta += diffusivity * (neighbour.getConcentrationContainer().get(MEMBRANE, entity) - currentConcentration);
        }
        // return delta
        return new ConcentrationDelta(this, supplier.getCurrentSubsection(), entity, delta);
    }

    @Override
    public void checkFeatures() {
        logger.debug("The module " + getClass().getSimpleName() + " requires the Feature MembraneDiffusivity to be annotated to all requested chemical entities.");
    }

    public interface EntityLimitationStep {
        EntityLimitationStep identifier(String identifier);

        SectionLimitationStep forEntity(ChemicalEntity chemicalEntity);

        SectionLimitationStep forAllEntities(ChemicalEntity... chemicalEntities);

        SectionLimitationStep forAllEntities(Collection<ChemicalEntity> chemicalEntities);

    }

    public interface SectionLimitationStep {

        BuildStep forMembrane(CellRegion subsection);

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

        private static void setDefaultFeatureIfNecessary(ChemicalEntity entity) {
            if (entity.hasFeature(MembraneDiffusivity.class)) {
                return;
            }
            entity.setFeature(DEFAULT_MEMBRANE_DIFFUSIVITY);
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

        public SectionLimitationStep forEntity(ChemicalEntity chemicalEntity) {
            return forAllEntities(Collections.singletonList(chemicalEntity));
        }

        public SectionLimitationStep forAllEntities(ChemicalEntity... chemicalEntities) {
            return forAllEntities(Arrays.asList(chemicalEntities));
        }

        public SectionLimitationStep forAllEntities(Collection<ChemicalEntity> chemicalEntities) {
            for (ChemicalEntity chemicalEntity : chemicalEntities) {
                setDefaultFeatureIfNecessary(chemicalEntity);
            }
            module.setFeature(new Cargoes(new ArrayList<>(chemicalEntities)));
            return this;
        }

        @Override
        public BuildStep forMembrane(CellRegion region) {
            if (region.hasMembrane()) {
                logger.warn("The supplied region {} has no membrane assigned subsection", region.getIdentifier());
            }
            module.setFeature(new AffectedRegion(region));
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
