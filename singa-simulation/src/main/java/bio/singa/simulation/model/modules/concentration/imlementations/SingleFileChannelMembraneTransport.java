package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.Transporter;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.ModuleFactory;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;
import tec.uom.se.quantity.Quantities;

import java.util.*;

/**
 * @author cl
 */
public class SingleFileChannelMembraneTransport extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    public static TransporterStep inSimulation(Simulation simulation) {
        return new SingleFileChannelMembraneTransportBuilder(simulation);
    }

    private Transporter transporter;
    private ChemicalEntity cargo;
    private Set<ChemicalEntity> solutes;

    private void initialize() {
        // apply
        setApplicationCondition(updatable -> true);
        // function
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(OsmoticPermeability.class);
        // reference entities for this module
        addReferencedEntity(transporter);
        addReferencedEntity(cargo);
        addReferencedEntities(solutes);
        // reference module in simulation
        addModuleToSimulation();
    }

    @Override
    public void checkFeatures() {
        if (!transporter.hasFeature(OsmoticPermeability.class)) {
            transporter.setFeature(OsmoticPermeability.class);
        }
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer container) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        final double permeability = getScaledFeature(transporter, OsmoticPermeability.class).getValue().doubleValue();
        final double value = getSoluteDelta(container) * permeability * MolarConcentration.concentrationToMolecules(container.get(CellTopology.MEMBRANE, transporter), Environment.getSubsectionVolume()).getValue().doubleValue();
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), container.getOuterSubsection(), cargo),
                new ConcentrationDelta(this, container.getOuterSubsection(), cargo, Quantities.getQuantity(value, Environment.getConcentrationUnit())));
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), container.getInnerSubsection(), cargo),
                new ConcentrationDelta(this, container.getInnerSubsection(), cargo, Quantities.getQuantity(-value, Environment.getConcentrationUnit())));
        return deltas;
    }

    private double getSoluteDelta(ConcentrationContainer container) {
        // sum outer solutes
        double outerConcentration = 0.0;
        for (ChemicalEntity solute : solutes) {
            outerConcentration += container.get(CellTopology.OUTER, solute).getValue().doubleValue();
        }
        // sum inner solutes
        double innerConcentration = 0.0;
        for (ChemicalEntity solute : solutes) {
            innerConcentration += container.get(CellTopology.INNER, solute).getValue().doubleValue();
        }
        // return delta
        return innerConcentration - outerConcentration;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + transporter.getName() + ")";
    }

    private boolean hasMembrane(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getSubsection(CellTopology.MEMBRANE) != null;
    }

    public interface TransporterStep {
        CargoStep transporter(Transporter transporter);
    }

    public interface CargoStep {
        SolutesStep cargo(ChemicalEntity cargo);
    }

    public interface SolutesStep {
        BuildStep forSolute(ChemicalEntity chemicalEntity);

        BuildStep forSolutes(ChemicalEntity... chemicalEntities);

        BuildStep forSolutes(Collection<ChemicalEntity> chemicalEntities);
    }

    public interface BuildStep {
        SingleFileChannelMembraneTransport build();
    }

    public static class SingleFileChannelMembraneTransportBuilder implements TransporterStep, CargoStep, SolutesStep, BuildStep {

        SingleFileChannelMembraneTransport module;

        public SingleFileChannelMembraneTransportBuilder(Simulation simulation) {
            module = ModuleFactory.setupModule(SingleFileChannelMembraneTransport.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
            module.setSimulation(simulation);
            module.solutes = new HashSet<>();
        }

        @Override
        public CargoStep transporter(Transporter transporter) {
            module.transporter = transporter;
            return this;
        }

        @Override
        public SolutesStep cargo(ChemicalEntity cargo) {
            module.cargo = cargo;
            return this;
        }

        @Override
        public BuildStep forSolute(ChemicalEntity solute) {
            module.solutes.add(solute);
            return this;
        }

        @Override
        public BuildStep forSolutes(ChemicalEntity... solutes) {
            module.solutes.addAll(Arrays.asList(solutes));
            return this;
        }

        @Override
        public BuildStep forSolutes(Collection<ChemicalEntity> solutes) {
            module.solutes.addAll(solutes);
            return this;
        }

        @Override
        public SingleFileChannelMembraneTransport build() {
            module.initialize();
            return module;
        }
    }

}
