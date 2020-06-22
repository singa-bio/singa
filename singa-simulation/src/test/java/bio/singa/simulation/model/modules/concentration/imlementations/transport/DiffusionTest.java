package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.simulation.entities.simple.SmallMolecule;
import bio.singa.features.quantities.ConcentrationDiffusivity;
import bio.singa.core.utility.Pair;
import bio.singa.core.utility.Resources;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.simulation.model.agents.surfacelike.GridImageReader;
import bio.singa.simulation.model.agents.surfacelike.GridMembraneBuilder;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.error.TimeStepManager;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static bio.singa.features.quantities.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.*;
import static bio.singa.simulation.model.sections.CellSubsections.CYTOPLASM;
import static bio.singa.simulation.model.sections.CellSubsections.EXTRACELLULAR_REGION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class DiffusionTest {

    private static final Logger logger = LoggerFactory.getLogger(DiffusionTest.class);

    private static final Quantity<Length> systemExtend = Quantities.getQuantity(2500.0, NANO(METRE));
    private static final double simulationExtend = 2500;

    // required species
    private static final SmallMolecule hydrogen = SmallMolecule.create("h2")
            .assignFeature(ConcentrationDiffusivity.of(4.40E-05, SQUARE_CENTIMETRE_PER_SECOND).build())
            .build();

    private static final SmallMolecule ammonia = SmallMolecule.create("ammonia")
            .assignFeature(ConcentrationDiffusivity.of(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND).build())
            .build();

    private static final SmallMolecule benzene = SmallMolecule.create("benzene")
            .assignFeature(ConcentrationDiffusivity.of(1.09E-05, SQUARE_CENTIMETRE_PER_SECOND).build())
            .build();

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    @DisplayName("diffusion of hydrogen with 10 nodes")
    void shouldReachCorrectHalfLife1() {
        // setup and run simulation
        Simulation simulation = setUpSimulation(10, hydrogen);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, 10, hydrogen);
        // test results
        assertEquals(Quantities.getQuantity(135, MICRO(SECOND)).getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    @DisplayName("diffusion of hydrogen with 20 nodes")
    void shouldReachCorrectHalfLife2() {
        // setup and run simulation
        Simulation simulation = setUpSimulation(20, hydrogen);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, 20, hydrogen);
        // test results
        assertEquals(Quantities.getQuantity(135, MICRO(SECOND)).getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        Environment.reset();
    }

    @Test
    @DisplayName("diffusion of ammonia with 30 nodes")
    @Disabled
    void shouldReachCorrectHalfLife3() {
        // setup and run simulation
        Simulation simulation = setUpSimulation(30, ammonia);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, 30, ammonia);
        // test results
        assertEquals(Quantities.getQuantity(261, MICRO(SECOND)).getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        Environment.reset();
    }

    @Test
    @DisplayName("diffusion of benzene with 30 nodes")
    @Disabled
    void shouldReachCorrectHalfLife4() {
        // setup and run simulation
        Simulation simulation = setUpSimulation(30, benzene);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, 30, benzene);
        // test results
        assertEquals(Quantities.getQuantity(539, MICRO(SECOND)).getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        Environment.reset();
    }

    @Test
    @DisplayName("diffusion of ammonia blocked by membrane")
    void shouldBlockDiffusionWithMembrane() {

        // create simulation
        Simulation simulation = new Simulation();

        GridImageReader imageReader = GridImageReader.readTemplate(Resources.getResourceAsFileLocation("grids/membrane_10x2.png"));
        int[][] sectionArray = imageReader.getGrid();

        Environment.setSimulationExtend(1000);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(1000, NANO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);

        GridMembraneBuilder gmb = new GridMembraneBuilder(sectionArray);
        int red = Color.RED.getRGB();
        int blue = Color.BLUE.getRGB();
        Map<Integer, CellRegion> subsectionMap = new HashMap<>();
        subsectionMap.put(red, CYTOPLASM_REGION);
        subsectionMap.put(blue, CellRegions.EXTRACELLULAR_REGION);

        Map<Pair<Integer>, CellRegion> membraneMap = new HashMap<>();
        membraneMap.put(new Pair<>(blue, red), CELL_OUTER_MEMBRANE_REGION);
        membraneMap.put(new Pair<>(red, blue), CELL_INNER_MEMBRANE_REGION);

        gmb.setRegionMapping(subsectionMap);
        gmb.setMembraneMapping(membraneMap);
        gmb.createTopology();

        AutomatonGraph graph = gmb.getGraph();
        simulation.setGraph(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        Membrane membrane = gmb.getMembranes().get(CELL_OUTER_MEMBRANE_REGION);
        membraneLayer.addMembrane(membrane);
        simulation.setMembraneLayer(membraneLayer);

        simulation.setGraph(graph);

        // set concentration
        ConcentrationBuilder.create(simulation)
                .entity(ammonia)
                .subsection(CYTOPLASM)
                .concentrationValue(1.0)
                .unit(MOLE_PER_LITRE)
                .updatableIdentifier("n(5,0)")
                .build();

        Diffusion.inSimulation(simulation)
                .forEntity(ammonia)
                .forAllSections()
                .build();

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getRow() == 1) {
                // nothing should permeate to the outer part
                assertEquals(0.0, node.getConcentrationContainer().get(EXTRACELLULAR_REGION, ammonia));
            } else {
                assertTrue(node.getConcentrationContainer().get(CYTOPLASM, ammonia) > 0.0);
            }
        }

    }

    private Simulation setUpSimulation(int numberOfNodes, SmallMolecule species) {
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        // setup node distance to diameter
        Environment.setNodeSpacingToDiameter(systemExtend, numberOfNodes);
        // setup rectangular graph with number of nodes
        Rectangle boundingBox = new Rectangle(Environment.getSimulationExtend(), Environment.getSimulationExtend());
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(numberOfNodes, numberOfNodes, boundingBox));
        // initialize species in graph with desired concentration leaving the right "half" empty
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() < (graph.getNumberOfColumns() / 2)) {
                node.getConcentrationContainer().initialize(EXTRACELLULAR_REGION, species, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
            } else {
                node.getConcentrationContainer().initialize(EXTRACELLULAR_REGION, species, Quantities.getQuantity(0.0, MOLE_PER_LITRE));
            }
        }
        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add diffusion module
        Diffusion.inSimulation(simulation)
                .forEntity(species)
                .forAllSections()
                .build();
        // return complete simulation
        return simulation;
    }

    private Quantity<Time> runSimulation(Simulation simulation, int numberOfNodes, SmallMolecule species) {
        // returns the node in the middle on the right
        RectangularCoordinate coordinate = new RectangularCoordinate(numberOfNodes - 1, (numberOfNodes / 2) - 1);
        simulation.getGraph().getNode(coordinate).setObserved(true);
        // simulate until half life concentration has been reached
        double currentConcentration = 0.0;
        while (currentConcentration < 0.25) {
            simulation.nextEpoch();
            currentConcentration = UnitRegistry.concentration(simulation.getGraph().getNode(coordinate).getConcentrationContainer().get(EXTRACELLULAR_REGION, species)).to(MOLE_PER_LITRE).getValue().doubleValue();
        }
        logger.info("Half life time of {} reached at {}.", species.getIdentifier(), TimeStepManager.getElapsedTime().to(MICRO(SECOND)));
        return TimeStepManager.getElapsedTime().to(MICRO(SECOND));
    }

}