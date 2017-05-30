package de.bioforscher.singa.simulation.modules.diffusion;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.parser.chebi.ChEBIParserService;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.util.GraphFactory;
import de.bioforscher.singa.mathematics.graphs.util.RectangularGridCoordinateConverter;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.BioEdge;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.units.parameters.EnvironmentalParameters;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity.SQUARE_CENTIMETER_PER_SECOND;
import static junit.framework.TestCase.assertEquals;
import static tec.units.ri.unit.MetricPrefix.MICRO;
import static tec.units.ri.unit.MetricPrefix.NANO;
import static tec.units.ri.unit.Units.METRE;
import static tec.units.ri.unit.Units.SECOND;

/**
 * @author cl
 */
public class FreeDiffusionTest {

    private final Rectangle defaultBoundingBox = new Rectangle(new Vector2D(0, 400), new Vector2D(400, 0));

    private Species hydrogen = ChEBIParserService.parse("CHEBI:18276");
    private Species ammonia = ChEBIParserService.parse("CHEBI:16134");
    private Species benzene = ChEBIParserService.parse("CHEBI:16716");

    private FreeDiffusion freeDiffusion = new FreeDiffusion();

    private void setUpSpecies() {
        // setup diffusion
        this.freeDiffusion = new FreeDiffusion();
        // fix diffusion values from literature
        this.freeDiffusion.fixDiffusionCoefficientForEntity(this.hydrogen, Quantities.getQuantity(4.40E-05,
                SQUARE_CENTIMETER_PER_SECOND));
        this.freeDiffusion.fixDiffusionCoefficientForEntity(this.ammonia, Quantities.getQuantity(2.28E-05,
                SQUARE_CENTIMETER_PER_SECOND));
        this.freeDiffusion.fixDiffusionCoefficientForEntity(this.benzene, Quantities.getQuantity(1.09E-05,
                SQUARE_CENTIMETER_PER_SECOND));
    }

    private Simulation setUpSimulation(int numberOfNodes, Quantity<Time> timeStep, Species species) {
        setUpSpecies();
        // setup rectangular graph with number of nodes
        AutomatonGraph graph = AutomatonGraphs.copyStructureToBioGraph(GraphFactory.buildGridGraph(
                numberOfNodes, numberOfNodes, this.defaultBoundingBox, false));

        // initialize species in graph with desired concentration leaving the right "half" empty
        for (BioNode node : graph.getNodes()) {
            if (node.getIdentifier() % numberOfNodes < numberOfNodes / 2) {
                node.setConcentration(species, 1.0);
            } else {
                node.setConcentration(species, 0.0);
            }
        }

        for (BioEdge edge : graph.getEdges()) {
            edge.addPermeability(species, 1.0);
        }

        // setup time step size as given
        EnvironmentalParameters.getInstance().setTimeStep(timeStep);
        // setup node distance to diameter / (numberOfNodes - 1)
        EnvironmentalParameters.getInstance().setNodeSpacingToDiameter(
                Quantities.getQuantity(2500.0, NANO(METRE)), numberOfNodes);

        // setup simulation
        Simulation simulation = new Simulation();
        // add graph
        simulation.setGraph(graph);

        // add diffusion module
        simulation.getModules().add(this.freeDiffusion);
        this.freeDiffusion.rescale();
        // add desired species to the simulation for easy access
        simulation.getChemicalEntities().add(species);

        return simulation;

    }

    public Quantity<Time> runSimulation(Simulation simulation, int numberOfNodes) {
        RectangularGridCoordinateConverter converter = new RectangularGridCoordinateConverter(numberOfNodes, numberOfNodes);

        int observedNodeIdentifier = converter.convert(new Vector2D(numberOfNodes - 1, (numberOfNodes / 2) - 1));
        System.out.println("Observing node: " + observedNodeIdentifier);
        simulation.getGraph().getNode(observedNodeIdentifier).setObserved(true);

        System.out.println("Starting simulation ...");
        double currentConcentration = 0.0;
        while (currentConcentration < 0.25) {
            simulation.nextEpoch();
            currentConcentration = simulation.getGraph().getNode(observedNodeIdentifier).getConcentration(this.hydrogen).getValue().doubleValue();
            if (simulation.getEpoch() % 1000 == 0 && simulation.getEpoch() > 1) {
                System.out.println("Currently at: " + simulation.getElapsedTime().to(MICRO(SECOND)) + " at concentration " + currentConcentration);
            }
        }

        // check correct diffusion
        System.out.println("Half life time of " + this.hydrogen.getName() + " reached at " + simulation.getElapsedTime().to(MICRO(SECOND)));
        return simulation.getElapsedTime().to(MICRO(SECOND));

    }

    // 10 ns, 10 nodes, hydrogen
    @Test
    public void shouldReachCorrectHalfLife10ns10nd() {
        // parameters
        int numberOfNodes = 10;
        Quantity<Time> timeStepSize = Quantities.getQuantity(10, NANO(SECOND));
        Species species = this.hydrogen;
        // setup and run simulation
        Simulation simulation = setUpSimulation(numberOfNodes, timeStepSize, species);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, numberOfNodes);
        // test results
        Quantity<Time> expectedHalfLifeTime = Quantities.getQuantity(165.91, MICRO(SECOND));
        assertEquals(expectedHalfLifeTime.getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 0.0);
    }

    // 10 ns, 20 nodes, hydrogen
    @Test
    public void shouldReachCorrectHalfLife10ns20nd() {
        // parameters
        int numberOfNodes = 20;
        Quantity<Time> timeStepSize = Quantities.getQuantity(10, NANO(SECOND));
        Species species = this.hydrogen;
        // setup and run simulation
        Simulation simulation = setUpSimulation(numberOfNodes, timeStepSize, species);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, numberOfNodes);
        // test results
        Quantity<Time> expectedHalfLifeTime = Quantities.getQuantity(149.0, MICRO(SECOND));
        assertEquals(expectedHalfLifeTime.getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 0.0);
    }

    // 10 ns, 50 nodes, hydrogen
    @Test
    public void shouldReachCorrectHalfLife10ns50nd() {
        // parameters
        int numberOfNodes = 50;
        Quantity<Time> timeStepSize = Quantities.getQuantity(10, NANO(SECOND));
        Species species = this.hydrogen;
        // setup and run simulation
        Simulation simulation = setUpSimulation(numberOfNodes, timeStepSize, species);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, numberOfNodes);
        // test results
        Quantity<Time> expectedHalfLifeTime = Quantities.getQuantity(140.04, MICRO(SECOND));
        assertEquals(expectedHalfLifeTime.getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 0.0);
    }

    // 20 ns, 20 nodes, hydrogen
    @Test
    public void shouldReachCorrectHalfLife20ns20nd() {
        // parameters
        int numberOfNodes = 10;
        Quantity<Time> timeStepSize = Quantities.getQuantity(20, NANO(SECOND));
        Species species = this.hydrogen;
        // setup and run simulation
        Simulation simulation = setUpSimulation(numberOfNodes, timeStepSize, species);
        Quantity<Time> actualHalfLifeTime = runSimulation(simulation, numberOfNodes);
        // test results
        Quantity<Time> expectedHalfLifeTime = Quantities.getQuantity(165.92, MICRO(SECOND));
        assertEquals(expectedHalfLifeTime.getValue().doubleValue(), actualHalfLifeTime.getValue().doubleValue(), 0.0);
    }
    // 50 ns, 10 nodes, hydrogen
    // 100 ns, 10 nodes, hydrogen

    // 10 ns, 50 nodes, ammonium
    // 10 ns, 50 nodes, benzene



}