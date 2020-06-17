package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.ConcentrationDiffusivity;
import bio.singa.simulation.features.AffectedRegion;
import bio.singa.simulation.features.Cargo;
import bio.singa.simulation.features.MembraneTickness;
import bio.singa.simulation.features.Ratio;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.*;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculates diffusion through large pores (pore significantly larger than the cargo) in the membrane.
 * J_s = -(D_w)*(A_p/A_m)*DC/l
 * J_S id th flux, D_W is the diffusivity of the cargo, A_p is the area of the pore, A_m is the area of the membrane,
 * DC is the difference in concentration between both sites of the membrane, l is the membrane thickness
 *
 * @author cl
 */
public class PorousDiffusion extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    private CellRegion region;
    private ChemicalEntity cargo;
    private double poreMembraneRatio;
    private double membraneThickness;

    public static FeatureStep inSimulation(Simulation simulation) {
        return new PorousDiffusionBuilder(simulation);
    }

    public PorousDiffusion() {

    }

    private void postConstruct() {
        // apply
        setApplicationCondition(this::isCorrectRegion);
        // function
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(AffectedRegion.class);
        getRequiredFeatures().add(Cargo.class);
        getRequiredFeatures().add(Ratio.class);
        getRequiredFeatures().add(MembraneTickness.class);
    }

    private CellRegion getRegion() {
        // constant over the course of the simulation
        if (region == null) {
            region = getFeature(AffectedRegion.class).getContent();
        }
        return region;
    }

    private ChemicalEntity getCargo() {
        // constant over the course of the simulation
        if (cargo == null) {
            cargo = getFeature(Cargo.class).getContent();
        }
        return cargo;
    }

    private double getPoreMembraneRatio() {
        // constant over the course of the simulation
        if (poreMembraneRatio == 0.0) {
            poreMembraneRatio = getFeature(Ratio.class).getContent().getValue().doubleValue();
        }
        return poreMembraneRatio;
    }

    private double getMembraneThickness() {
        if (membraneThickness == 0.0) {
            membraneThickness = getFeature(MembraneTickness.class).getContent().getValue().doubleValue();
        }
        return membraneThickness;
    }

    private boolean isCorrectRegion(Updatable updatable) {
        return updatable.getCellRegion().equals(getRegion());
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer container) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        Updatable currentUpdatable = supplier.getCurrentUpdatable();
        double value = calculateVelocity(container);
        deltas.put(new ConcentrationDeltaIdentifier(currentUpdatable, container.getInnerSubsection(), getCargo()),
                new ConcentrationDelta(this, container.getInnerSubsection(), getCargo(), value));
        deltas.put(new ConcentrationDeltaIdentifier(currentUpdatable, container.getOuterSubsection(), getCargo()),
                new ConcentrationDelta(this, container.getOuterSubsection(), getCargo(), -value));
        return deltas;
    }


    private double calculateVelocity(ConcentrationContainer container) {
        double cargoDifference = getCargoDifference(container);
        double membraneArea = ((AutomatonNode) supplier.getCurrentUpdatable()).getMembraneArea().getValue().doubleValue();
        double diffusivity = getScaledFeature(getCargo(), ConcentrationDiffusivity.class);
        return diffusivity * getPoreMembraneRatio() * membraneArea * (cargoDifference / getMembraneThickness());
    }

    private double getCargoDifference(ConcentrationContainer container) {
        return container.get(CellTopology.OUTER, getCargo()) - container.get(CellTopology.INNER, getCargo());
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new PorousDiffusionBuilder(simulation);
    }

    public interface FeatureStep {
        BuildStep identifier(String identifier);

        FeatureStep cargo(Cargo chemicalEntity);

        FeatureStep region(AffectedRegion region);

        FeatureStep poreMembraneRatio(Ratio ratio);

        FeatureStep membraneThickness(MembraneTickness membraneTickness);

    }

    public interface BuildStep {
        PorousDiffusion build();
    }

    public static class PorousDiffusionBuilder implements FeatureStep, BuildStep, ModuleBuilder<PorousDiffusion> {

        PorousDiffusion module;
        private Simulation simulation;

        public PorousDiffusionBuilder(Simulation simulation) {
            this.simulation = simulation;
            createModule(simulation);
        }

        @Override
        public PorousDiffusion getModule() {
            return module;
        }

        @Override
        public BuildStep identifier(String identifier) {
            module.setIdentifier(identifier);
            return this;
        }

        @Override
        public FeatureStep cargo(Cargo cargo) {
            module.setFeature(cargo);
            return this;
        }

        @Override
        public FeatureStep region(AffectedRegion region) {
            module.setFeature(region);
            return this;
        }

        @Override
        public FeatureStep poreMembraneRatio(Ratio ratio) {
            module.setFeature(ratio);
            return this;
        }

        @Override
        public FeatureStep membraneThickness(MembraneTickness membraneTickness) {
            module.setFeature(membraneTickness);
            return this;
        }

        @Override
        public PorousDiffusion createModule(Simulation simulation) {
            module = ModuleFactory.setupModule(PorousDiffusion.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
            return module;
        }

        public PorousDiffusion build() {
            module.postConstruct();
            simulation.addModule(module);
            return module;
        }

    }


}
