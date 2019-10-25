package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureProvider;
import bio.singa.simulation.features.AffectedSection;
import bio.singa.simulation.features.Cargoes;
import bio.singa.simulation.features.Ratio;
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

    private CellSubsection restrictedSubsection;

    public Diffusion() {

    }

    public static EntityLimitationStep inSimulation(Simulation simulation) {
        return new DiffusionBuilder(simulation);
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new DiffusionBuilder(simulation);
    }

    private void postConstruct() {
        // apply
        setApplicationCondition(updatable -> updatable instanceof AutomatonNode);
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
        getRequiredFeatures().add(Diffusivity.class);
        getRequiredFeatures().add(Ratio.class);
        List<ChemicalEntity> cargoes = getFeature(Cargoes.class).getContent();
        addReferencedEntities(cargoes);
    }

    private ConcentrationDelta calculateDelta(ConcentrationContainer concentrationContainer) {
        AutomatonNode node = (AutomatonNode) supplier.getCurrentUpdatable();
        ChemicalEntity entity = supplier.getCurrentEntity();
        CellSubsection subsection = supplier.getCurrentSubsection();
        final double currentConcentration = concentrationContainer.get(subsection, entity);
        final double diffusivity = getScaledFeature(entity, Diffusivity.class);
        // traverse each neighbouring subsection
        List<AutomatonNode.AreaMapping> areaMappings = node.getSubsectionAdjacency().get(subsection);
        double delta = 0.0;
        for (AutomatonNode.AreaMapping mapping : areaMappings) {
            double partialDelta = 0.0;
            if (mapping.isCached()) {
                partialDelta = mapping.getCached();
            } else {
                AutomatonNode other = mapping.getOther(node);
                ConcentrationContainer otherContainer;
                if (supplier.isStrutCalculation()) {
                    otherContainer = getScope().getHalfStepConcentration(other);
                } else {
                    otherContainer = other.getConcentrationContainer();
                }
                partialDelta = diffusivity * mapping.getRelativeArea() * mapping.getDiffusiveRatio() * (otherContainer.get(mapping.getSubsection(), entity) - currentConcentration);
            }
            delta += partialDelta;
            mapping.setCache(partialDelta);
        }
        // return delta
        return new ConcentrationDelta(this, subsection, entity, delta);
    }

    private boolean unrestrictedApplication(ConcentrationContainer container) {
        return !supplier.getCurrentSubsection().isMembrane();
    }

    private boolean restrictedApplication(ConcentrationContainer container) {
        return supplier.getCurrentSubsection().equals(restrictedSubsection);
    }

    @Override
    public void checkFeatures() {
        logger.debug("The module " + getClass().getSimpleName() + " requires the Feature Diffusivity to be annotated to all requested chemical entities.");
    }

    @Override
    public void inBetweenHalfSteps() {
        getSimulation().getGraph().getNodes().forEach(AutomatonNode::clearCaches);
    }

    public interface EntityLimitationStep {
        EntityLimitationStep identifier(String identifier);

        SectionLimitationStep forEntity(ChemicalEntity chemicalEntity);

        SectionLimitationStep forAllEntities(ChemicalEntity... chemicalEntities);

        SectionLimitationStep forAllEntities(Collection<ChemicalEntity> chemicalEntities);

    }

    public interface SectionLimitationStep {

        BuildStep forSection(CellSubsection subsection);

        BuildStep forAllSections();

        SectionLimitationStep withReducedRatio(Ratio ratio);

    }

    public interface BuildStep {
        Diffusion build();
    }

    public static class DiffusionBuilder implements EntityLimitationStep, SectionLimitationStep, BuildStep, ModuleBuilder<Diffusion> {

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

        public SectionLimitationStep forEntity(ChemicalEntity chemicalEntity) {
            return forAllEntities(Collections.singletonList(chemicalEntity));
        }

        public SectionLimitationStep forAllEntities(ChemicalEntity... chemicalEntities) {
            return forAllEntities(Arrays.asList(chemicalEntities));
        }

        public SectionLimitationStep forAllEntities(Collection<ChemicalEntity> chemicalEntities) {
            module.setFeature(new Cargoes(new ArrayList<>(chemicalEntities)));
            return this;
        }

        @Override
        public BuildStep forSection(CellSubsection subsection) {
            module.setFeature(new AffectedSection(subsection));
            return this;
        }

        @Override
        public BuildStep forAllSections() {
            return this;
        }

        @Override
        public SectionLimitationStep withReducedRatio(Ratio ratio) {
            module.setFeature(ratio);
            return this;
        }

        public Diffusion build() {
            module.postConstruct();
            simulation.addModule(module);
            return module;
        }

    }

}
