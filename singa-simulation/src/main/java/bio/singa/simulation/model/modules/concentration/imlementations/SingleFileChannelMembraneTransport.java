package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
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

import java.util.*;

import static bio.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;

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
    private Set<ChemicalEntity> solutes;

    private void initialize() {
        // apply
        setApplicationCondition(updatable -> updatable.getCellRegion().hasMembrane());
        // function
        UpdatableDeltaFunction function = new UpdatableDeltaFunction(this::calculateDeltas, container -> true);
        addDeltaFunction(function);
        // feature
        getRequiredFeatures().add(OsmoticPermeability.class);
        // reference entities for this module
        transporter = getFeature(Transporter.class).getFeatureContent();
        addReferencedEntity(transporter);
        cargo = getFeature(Cargo.class).getFeatureContent();
        addReferencedEntity(cargo);
        solutes = getFeature(Solutes.class).getFeatureContent();
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
        final double value = getSoluteDelta(container) * permeability * MolarConcentration.concentrationToMolecules(container.get(CellTopology.MEMBRANE, transporter)).getValue().doubleValue();
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), container.getInnerSubsection(), cargo),
                new ConcentrationDelta(this, container.getInnerSubsection(), cargo, UnitRegistry.concentration(value)));
        deltas.put(new ConcentrationDeltaIdentifier(supplier.getCurrentUpdatable(), container.getOuterSubsection(), cargo),
                new ConcentrationDelta(this, container.getOuterSubsection(), cargo, UnitRegistry.concentration(-value)));
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

    public static ModuleBuilder getBuilder(Simulation simulation) {
        return new SingleFileChannelMembraneTransportBuilder(simulation);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + transporter.getName() + ")";
    }

    private boolean hasMembrane(ConcentrationContainer concentrationContainer) {
        return concentrationContainer.getSubsection(CellTopology.MEMBRANE) != null;
    }

    public interface TransporterStep {
        CargoStep transporter(Protein transporter);
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

    public static class SingleFileChannelMembraneTransportBuilder implements TransporterStep, CargoStep, SolutesStep, BuildStep, ModuleBuilder {

        SingleFileChannelMembraneTransport module;

        public SingleFileChannelMembraneTransportBuilder(Simulation simulation) {
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
        public CargoStep transporter(Protein transporter) {
            module.setFeature(new Transporter(transporter, MANUALLY_ANNOTATED));
            return this;
        }

        @Override
        public SolutesStep cargo(ChemicalEntity cargo) {
            module.setFeature(new Cargo(cargo, MANUALLY_ANNOTATED));
            return this;
        }

        @Override
        public BuildStep forSolute(ChemicalEntity solute) {
            module.setFeature(new Solutes(Collections.singleton(solute), MANUALLY_ANNOTATED));
            return this;
        }

        @Override
        public BuildStep forSolutes(ChemicalEntity... solutes) {
            module.setFeature(new Solutes(new HashSet<>(Arrays.asList(solutes)), MANUALLY_ANNOTATED));
            return this;
        }

        @Override
        public BuildStep forSolutes(Collection<ChemicalEntity> solutes) {
            module.setFeature(new Solutes(new HashSet<>(solutes), MANUALLY_ANNOTATED));
            return this;
        }

        @Override
        public SingleFileChannelMembraneTransport build() {
            module.initialize();
            return module;
        }
    }

}
