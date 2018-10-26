package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class DiffusionUnhinderedTest {

    private static final Logger logger = LoggerFactory.getLogger(DiffusionUnhinderedTest.class);

    private static final Quantity<Length> systemDiameter = Quantities.getQuantity(2500.0, NANO(METRE));

    // required species
    private static final SmallMolecule hydrogen = new SmallMolecule.Builder("h2")
            .name("dihydrogen")
            .assignFeature(new Diffusivity(Quantities.getQuantity(4.40E-05, SQUARE_CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    private static final SmallMolecule ammonia = new SmallMolecule.Builder("ammonia")
            .name("ammonia")
            .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    private static final SmallMolecule benzene = new SmallMolecule.Builder("benzene")
            .name("benzene")
            .assignFeature(new Diffusivity(Quantities.getQuantity(1.09E-05, SQUARE_CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
    }

    @Test
    void shouldReachCorrectHalfLife1() {
        // setup and run simulation
        Simulation simulation = setUpSimulation(10, hydrogen);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, 10, hydrogen);
        // test results
        assertEquals(Quantities.getQuantity(130, MICRO(SECOND)).getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        Environment.reset();
    }

    @Test
    void shouldReachCorrectHalfLife2() {
        // setup and run simulation
        Simulation simulation = setUpSimulation(20, hydrogen);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, 20, hydrogen);
        // test results
        assertEquals(Quantities.getQuantity(134, MICRO(SECOND)).getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        Environment.reset();
    }

    @Test
    void shouldReachCorrectHalfLife3() {
        // setup and run simulation
        Simulation simulation = setUpSimulation(30, ammonia);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, 30, ammonia);
        // test results
        assertEquals(Quantities.getQuantity(260, MICRO(SECOND)).getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        Environment.reset();
    }

    @Test
    void shouldReachCorrectHalfLife4() {
        // setup and run simulation
        Simulation simulation = setUpSimulation(30, benzene);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, 30, benzene);
        // test results
        assertEquals(Quantities.getQuantity(544, MICRO(SECOND)).getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        Environment.reset();
    }

    private Simulation setUpSimulation(int numberOfNodes, SmallMolecule species) {
        // setup node distance to diameter
        Environment.setNodeSpacingToDiameter(systemDiameter, numberOfNodes);
        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(numberOfNodes, numberOfNodes));
        // initialize species in graph with desired concentration leaving the right "half" empty
        for (AutomatonNode node : graph.getNodes()) {
            node.getConcentrationContainer().initializeSubsection(SECTION_A, INNER);
            if (node.getIdentifier().getColumn() < (graph.getNumberOfColumns() / 2)) {
                node.getConcentrationContainer().initialize(SECTION_A, species, Quantities.getQuantity(1.0, MOLE_PER_LITRE));
            } else {
                node.getConcentrationContainer().initialize(SECTION_A, species, Quantities.getQuantity(0.0, MOLE_PER_LITRE));
            }
        }
        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add diffusion module
        Diffusion.inSimulation(simulation)
                .onlyFor(species)
                .build();
        // return complete simulation
        return simulation;
    }

    private Quantity<Time> runSimulation(Simulation simulation, int numberOfNodes, SmallMolecule species) {
        // returns the node in the middle on the right
        RectangularCoordinate coordinate = new RectangularCoordinate(numberOfNodes - 1, (numberOfNodes / 2) - 1);
        // simulate until half life concentration has been reached
        double currentConcentration = 0.0;
        while (currentConcentration < 0.25) {
            simulation.nextEpoch();
            final Quantity<MolarConcentration> concentration = simulation.getGraph().getNode(coordinate).getConcentration(SECTION_A, species).to(MOLE_PER_LITRE);
            currentConcentration = concentration.getValue().doubleValue();
            //System.out.println("Currently "+concentration+" at "+simulation.getElapsedTime().to(MICRO(SECOND)));
        }
        logger.info("Half life time of {} reached at {}.", species.getName(), simulation.getElapsedTime().to(MICRO(SECOND)));
        return simulation.getElapsedTime().to(MICRO(SECOND));
    }

}