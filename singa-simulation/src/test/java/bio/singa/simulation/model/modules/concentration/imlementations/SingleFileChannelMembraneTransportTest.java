package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.SingleFileChannelMembraneTransport;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.features.DefaultFeatureSources.BINESH2015;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
class SingleFileChannelMembraneTransportTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    void shouldSimulateChannelDiffusion() {
        UnitRegistry.setSpace(Quantities.getQuantity(1, MICRO(METRE)));
        Simulation simulation = new Simulation();
        // setup species
        // water
        SmallMolecule water = SmallMolecule.create("water")
                .build();
        // solutes
        SmallMolecule solute = SmallMolecule.create("solutes")
                .build();
        // aqp2
        Protein aquaporin2 = Protein.create("aqp2")
                .assignFeature(new OsmoticPermeability(5.31e-14, BINESH2015))
                .build();
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        // set concentrations
        double aqp2Concentration = MolarConcentration.moleculesToConcentration(3700);
        node.getConcentrationContainer().initialize(CellTopology.OUTER, water, Quantities.getQuantity(50.0, MOLE_PER_LITRE));
        node.getConcentrationContainer().initialize(CellTopology.OUTER, solute, Quantities.getQuantity(0.2, MOLE_PER_LITRE));
        node.getConcentrationContainer().initialize(CellTopology.MEMBRANE, aquaporin2, UnitRegistry.concentration(aqp2Concentration));
        node.getConcentrationContainer().initialize(CellTopology.INNER, solute, Quantities.getQuantity(0.1, MOLE_PER_LITRE));
        node.getConcentrationContainer().initialize(CellTopology.INNER, water, Quantities.getQuantity(50.0, MOLE_PER_LITRE));
        // single file channel membrane transport
        SingleFileChannelMembraneTransport.inSimulation(simulation)
                .transporter(aquaporin2)
                .cargo(water)
                .forSolute(solute)
                .build();
        // simulate a couple of epochs
        double previousInnerConcentration = UnitRegistry.convert(Quantities.getQuantity(50.0, MOLE_PER_LITRE)).getValue().doubleValue();
        double previousOuterConcentration = 0.0;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // inner assertions
            double currentInnerConcentration = node.getConcentrationContainer().get(CellTopology.INNER, water);
            assertTrue(currentInnerConcentration < previousInnerConcentration);
            previousInnerConcentration = currentInnerConcentration;
            // outer assertions
            double currentOuterConcentration = node.getConcentrationContainer().get(CellTopology.OUTER, water);
            assertTrue(currentOuterConcentration > previousOuterConcentration);
            previousOuterConcentration = currentOuterConcentration;
        }
    }

}
