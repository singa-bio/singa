package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.features.Cargo;
import bio.singa.simulation.features.Solutes;
import bio.singa.simulation.features.Transporter;
import bio.singa.simulation.model.modules.concentration.*;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.modules.concentration.scope.IndependentUpdate;
import bio.singa.simulation.model.modules.concentration.specifity.UpdatableSpecific;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The single file channel transport describes the movement of cargo molecules through {@link Transporter} proteins, so
 * called membrane channels. In the channels the cargo proteins move in a single file configuration.
 * The kinetics of the transport is guided by the osmotic pressure of solute molecules as well as the
 * {@link OsmoticPermeability} of the transporter.
 * The net flux of the cargo through the membrane is:
 * <pre>
 *  JC = pf * nt * (s1 - s2)</pre>
 * where pf is the {@link OsmoticPermeability}, nt is the number of transporters in the membrane and s1 and s2 are the
 * concentrations of the solute on both sides of the membrane, as in:
 * <pre>
 *  Finkelstein, Alan. "Water movement through membrane channels."
 *  Current Topics in Membranes and Transport. Vol. 21. Academic Press, 1984. 295-308. </pre>
 * This concentration based module applies {@link IndependentUpdate}s and is {@link UpdatableSpecific}.
 * <pre>
 *  // get water from chebi
 *  SmallMolecule water = ChEBIParserService.parse("CHEBI:15377", "water");
 *
 *  // define solutes as a single species
 *  SmallMolecule solute = new SmallMolecule.Builder("solutes")
 *         .name("solutes")
 *         .build();
 *
 *  // aquaporin 2 as a transporter
 *  Transporter aquaporin2 = UniProtParserService.parse("P41181", "aqp2").asTransporter();
 *  aquaporin2.setFeature(new OsmoticPermeability(5.31e-14, BINESH2015));
 *
 *  // create module
 *  SingleFileChannelMembraneTransport.inSimulation(simulation)
 *         .transporter(aquaporin2)
 *         .cargo(water)
 *         .forSolute(solute)
 *         .build(); </pre>
 *
 * @author cl
 */
public class SingleFileChannelMembraneTransport extends ConcentrationBasedModule<UpdatableDeltaFunction> {

    public static TransporterStep inSimulation(Simulation simulation) {
        return new SingleFileChannelMembraneTransportBuilder(simulation);
    }

    private ChemicalEntity transporter;
    private ChemicalEntity cargo;
    private List<ChemicalEntity> solutes;

    public SingleFileChannelMembraneTransport() {

    }

    private void postConstruct() {
        // apply
        setApplicationCondition(updatable -> updatable.getCellRegion().hasMembrane());
        // function
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(OsmoticPermeability.class);
    }

    public void initialize() {
        // reference entities for this module
        transporter = getFeature(Transporter.class).getContent();
        addReferencedEntity(transporter);
        cargo = getFeature(Cargo.class).getContent();
        addReferencedEntity(cargo);
        solutes = getFeature(Solutes.class).getContent();
        addReferencedEntities(solutes);
    }

    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> calculateDeltas(ConcentrationContainer container) {
        Map<ConcentrationDeltaIdentifier, ConcentrationDelta> deltas = new HashMap<>();
        final double permeability = getScaledFeature(transporter, OsmoticPermeability.class);
        final double value = getSoluteDelta(container) * permeability * MolarConcentration.concentrationToMolecules(container.get(CellTopology.MEMBRANE, transporter)).getValue().doubleValue();
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), container.getInnerSubsection(), cargo),
                new ConcentrationDelta(this, container.getInnerSubsection(), cargo, value));
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), container.getOuterSubsection(), cargo),
                new ConcentrationDelta(this, container.getOuterSubsection(), cargo, -value));
        return deltas;
    }

    private double getSoluteDelta(ConcentrationContainer container) {
        // sum outer solutes
        double outerConcentration = 0.0;
        for (ChemicalEntity solute : solutes) {
            outerConcentration += container.get(CellTopology.OUTER, solute);
        }
        // sum inner solutes
        double innerConcentration = 0.0;
        for (ChemicalEntity solute : solutes) {
            innerConcentration += container.get(CellTopology.INNER, solute);
        }
        // return delta
        return innerConcentration - outerConcentration;
    }

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new SingleFileChannelMembraneTransportBuilder(simulation);
    }

    private boolean hasMembrane(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getSubsection(CellTopology.MEMBRANE) != null;
    }

    public interface TransporterStep {
        CargoStep transporter(ChemicalEntity transporter);
    }

    public interface CargoStep {
        SolutesStep cargo(ChemicalEntity cargo);
    }

    public interface SolutesStep {
        BuildStep forSolute(ChemicalEntity chemicalEntity);

        BuildStep forSolutes(ChemicalEntity... chemicalEntities);

        BuildStep forSolutes(List<ChemicalEntity> chemicalEntities);
    }

    public interface BuildStep {
        SingleFileChannelMembraneTransport build();
    }

    public static class SingleFileChannelMembraneTransportBuilder implements TransporterStep, CargoStep, SolutesStep, BuildStep, ModuleBuilder {

        SingleFileChannelMembraneTransport module;
        private Simulation simulation;

        public SingleFileChannelMembraneTransportBuilder(Simulation simulation) {
            this.simulation = simulation;
            createModule(simulation);
        }

        @Override
        public SingleFileChannelMembraneTransport createModule(Simulation simulation) {
            module = ModuleFactory.setupModule(SingleFileChannelMembraneTransport.class,
                    ModuleFactory.Scope.NEIGHBOURHOOD_INDEPENDENT,
                    ModuleFactory.Specificity.UPDATABLE_SPECIFIC);
            module.setSimulation(simulation);
            return module;
        }

        @Override
        public SingleFileChannelMembraneTransport getModule() {
            return module;
        }


        @Override
        public CargoStep transporter(ChemicalEntity transporter) {
            module.setFeature(Transporter.of(transporter).build());
            return this;
        }

        @Override
        public SolutesStep cargo(ChemicalEntity cargo) {
            module.setFeature(Cargo.of(cargo).build());
            return this;
        }

        @Override
        public BuildStep forSolute(ChemicalEntity solute) {
            module.setFeature(Solutes.of(solute).build());
            return this;
        }

        @Override
        public BuildStep forSolutes(ChemicalEntity... solutes) {
            module.setFeature(Solutes.of(solutes).build());
            return this;
        }

        @Override
        public BuildStep forSolutes(List<ChemicalEntity> solutes) {
            module.setFeature(Solutes.of(solutes).build());
            return this;
        }

        @Override
        public SingleFileChannelMembraneTransport build() {
            module.postConstruct();
            simulation.addModule(module);
            return module;
        }
    }

}
