package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tec.units.indriya.ComparableQuantity;
import tec.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tec.units.indriya.unit.MetricPrefix.MICRO;
import static tec.units.indriya.unit.MetricPrefix.NANO;
import static tec.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
class VesicleCytoplasmDiffusionTest {

    @Test
    @DisplayName("vesicle diffusion - should move at all")
    void testVesicleDiffusion() {
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(20, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(500);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);

        Simulation simulation = new Simulation();

        Vector2D previousPosition = new Vector2D(220, 220);
        Vesicle vesicle = new Vesicle(previousPosition, Quantities.getQuantity(150, NANO(METRE)));

        // add vesicle transport layer
        VesicleLayer layer = new VesicleLayer(simulation);
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);

        // diffusion module
        VesicleCytoplasmDiffusion vesicleDiffusion = new VesicleCytoplasmDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            Vector2D currentPosition = simulation.getVesicleLayer().getVesicles().get(0).getPosition();
            assertNotEquals(currentPosition, previousPosition);
            previousPosition = currentPosition;
        }
    }

    @Test
    @DisplayName("vesicle diffusion - should move the correct distance")
    void shouldScaleDisplacementCorrectly() {
        // new simulation
        Simulation simulation = new Simulation();
        // setup environment
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(10, MICRO(METRE));
        Environment.setNodeSpacingToDiameter(systemExtend, 10);
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(100);
        // initialize vesicle
        Vector2D initialPosition = new Vector2D(50, 50);
        Vesicle vesicle = new Vesicle(initialPosition, Quantities.getQuantity(100, NANO(METRE)));
        // add vesicle transport layer
        VesicleLayer layer = new VesicleLayer(simulation);
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);
        // define graph
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);
        // diffusion module
        VesicleCytoplasmDiffusion vesicleDiffusion = new VesicleCytoplasmDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);
        // check distance travelled
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            Vector2D currentPosition = simulation.getVesicleLayer().getVesicles().iterator().next().getPosition();
            double distance = initialPosition.subtract(currentPosition).getMagnitude()/UnitRegistry.getTimeScale();
            // variance is high because of random gaussian
            assertEquals(0.000346, distance, 8e-3);
            initialPosition = currentPosition;
        }
    }
}