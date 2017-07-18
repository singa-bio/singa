package de.bioforscher.singa.simulation.modules.diffusion;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Graphs;
import de.bioforscher.singa.mathematics.graphs.model.GridCoordinateConverter;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.Arrays;
import java.util.Collection;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static junit.framework.TestCase.assertEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static tec.units.ri.unit.MetricPrefix.MICRO;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
@RunWith(Parameterized.class)
public class DiffusionProgressionTest {

    private static final Logger logger = LoggerFactory.getLogger(DiffusionProgressionTest.class);

    private static final Rectangle boundingBox = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));
    private static final Quantity<Length> systemDiameter = Quantities.getQuantity(2500.0, NANO(METRE));

    // required species
    private static Species hydrogen = new Species.Builder("h2")
            .name("dihydrogen")
            .assignFeature(new Diffusivity(Quantities.getQuantity(4.40E-05, SQUARE_CENTIMETER_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    private static Species ammonia = new Species.Builder("ammonia")
            .name("ammonia")
            .assignFeature(new Diffusivity(Quantities.getQuantity(2.28E-05, SQUARE_CENTIMETER_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    private static Species benzene = new Species.Builder("benzene")
            .name("benzene")
            .assignFeature(new Diffusivity(Quantities.getQuantity(1.09E-05, SQUARE_CENTIMETER_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                /* species, time step, number of nodes, expected result */
                /* test different numbers of nodes (10, 20, 50)*/
                /* 0 */ {hydrogen, Quantities.getQuantity(10, NANO(SECOND)), 10, Quantities.getQuantity(165.91, MICRO(SECOND))},
                /* 1 */ {hydrogen, Quantities.getQuantity(10, NANO(SECOND)), 20, Quantities.getQuantity(149.0, MICRO(SECOND))},
                /* 2 */ {hydrogen, Quantities.getQuantity(10, NANO(SECOND)), 50, Quantities.getQuantity(140.04, MICRO(SECOND))},
                /* test different time step sizes (20 ns, 50 ns, 100 ns)*/
                /* 3 */ {hydrogen, Quantities.getQuantity(20, NANO(SECOND)), 20, Quantities.getQuantity(149.0, MICRO(SECOND))},
                /* 4 */ {hydrogen, Quantities.getQuantity(50, NANO(SECOND)), 20, Quantities.getQuantity(149.0, MICRO(SECOND))},
                /* 5 */ {hydrogen, Quantities.getQuantity(100, NANO(SECOND)), 20, Quantities.getQuantity(149.0, MICRO(SECOND))},
                /* test different species (ammonia, benzene)*/
                /* 6 */ {ammonia, Quantities.getQuantity(10, NANO(SECOND)), 50, Quantities.getQuantity(270.25, MICRO(SECOND))},
                /* 7 */ {benzene, Quantities.getQuantity(10, NANO(SECOND)), 50, Quantities.getQuantity(565.3, MICRO(SECOND))}
        });
    }

    @Parameter(0)
    public Species species;

    @Parameter(1)
    public Quantity<Time> timeStep;

    @Parameter(2)
    public int numberOfNodes;

    @Parameter(3)
    public Quantity<Time> expectedOutcome;

    @Test
    public void shouldReachCorrectHalfLife() {
        logger.info("Performing free diffusion test for {} with a time step of {} and {} nodes ...", this.species.getName(), this.timeStep, this.numberOfNodes);
        // setup and run simulation
        Simulation simulation = setUpSimulation(this.numberOfNodes, this.timeStep, this.species);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, this.numberOfNodes, this.species);
        // test results
        assertEquals(this.expectedOutcome.getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 1e-5);
    }

    private Simulation setUpSimulation(int numberOfNodes, Quantity<Time> timeStep, Species species) {
        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(Graphs.buildGridGraph(
                numberOfNodes, numberOfNodes, boundingBox, false));
        // initialize species in graph with desired concentration leaving the right "half" empty
        for (BioNode node : graph.getNodes()) {
            if (node.getIdentifier() % numberOfNodes < numberOfNodes / 2) {
                node.setConcentration(species, 1.0);
            } else {
                node.setConcentration(species, 0.0);
            }
        }
        // setup time step size as given
        EnvironmentalParameters.getInstance().setTimeStep(timeStep);
        // setup node distance to diameter
        EnvironmentalParameters.getInstance().setNodeSpacingToDiameter(systemDiameter, numberOfNodes);
        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);
        // add diffusion module
        simulation.getModules().add(new FreeDiffusion());
        // return complete simulation
        return simulation;
    }

    private Quantity<Time> runSimulation(Simulation simulation, int numberOfNodes, Species species) {
        // grants access to easier access to grid coordinates
        RectangularGridCoordinateConverter converter = new RectangularGridCoordinateConverter(numberOfNodes, numberOfNodes);
        // observe the node in the middle on the right
        GridCoordinateConverter converter = new GridCoordinateConverter(numberOfNodes, numberOfNodes);
        // returns the node in the middle on the right
        int observedNodeIdentifier = converter.convert(new Vector2D(numberOfNodes - 1, (numberOfNodes / 2) - 1));
        simulation.getGraph().getNode(observedNodeIdentifier).setObserved(true);
        // simulate until half life concentration has been reached
        double currentConcentration = 0.0;
        while (currentConcentration < 0.25) {
            simulation.nextEpoch();
            final Quantity<MolarConcentration> concentration = simulation.getGraph().getNode(observedNodeIdentifier).getConcentration(species);
            currentConcentration = concentration.getValue().doubleValue();
        }
        logger.info("Half life time of {} reached at {}.", species.getName(), simulation.getElapsedTime().to(MICRO(SECOND)));
        return simulation.getElapsedTime().to(MICRO(SECOND));
    }

}