package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.Arrays;
import java.util.Collection;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellSubsection.SECTION_A;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;


/**
 * @author cl
 */
@RunWith(Parameterized.class)
public class DiffusionUnhinderedTest {

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

    @Parameter(0)
    public SmallMolecule species;
    @Parameter(1)
    public int numberOfNodes;
    @Parameter(2)
    public Quantity<Time> expectedOutcome;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                /* species, number of nodes (node distance), expected result */
                /* test different numbers of nodes (10, 20, 30)*/
                /* 0 */ {hydrogen, 10, Quantities.getQuantity(130, MICRO(SECOND))},
                /* 1 */ {hydrogen, 20, Quantities.getQuantity(134, MICRO(SECOND))},
                /* 2 */ {hydrogen, 30, Quantities.getQuantity(134, MICRO(SECOND))},
                /* test different species (ammonia, benzene)*/
                /* 3 */ {ammonia, 30, Quantities.getQuantity(260, MICRO(SECOND))},
                /* 4 */ {benzene, 30, Quantities.getQuantity(544, MICRO(SECOND))}
        });
    }

    @Test
    public void shouldReachCorrectHalfLife() {
        logger.info("Performing free diffusion test for {} with {}x{} nodes ...", species.getName(), numberOfNodes, numberOfNodes);
        // setup and run simulation
        Simulation simulation = setUpSimulation(numberOfNodes, species);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, numberOfNodes, species);
        // test results
        assertEquals(expectedOutcome.getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1);
        Environment.reset();
    }

    private Simulation setUpSimulation(int numberOfNodes, SmallMolecule species) {
        // setup node distance to diameter
        Environment.setNodeSpacingToDiameter(systemDiameter, numberOfNodes);
        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.useStructureFrom(Graphs.buildGridGraph(numberOfNodes, numberOfNodes));
        // initialize species in graph with desired concentration leaving the right "half" empty
        for (AutomatonNode node : graph.getNodes()) {
            if (node.getIdentifier().getColumn() < (graph.getNumberOfColumns() / 2)) {
                node.getConcentrationContainer().set(SECTION_A, species, 1.0);
            } else {
                node.getConcentrationContainer().set(SECTION_A, species, 0.0);
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
        simulation.getGraph().getNode(coordinate).setObserved(true);
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