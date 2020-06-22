package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.features.quantities.PixelDiffusivity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.error.TimeStepManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.features.quantities.Diffusivity.SQUARE_MICROMETRE_PER_SECOND;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class VesicleCytoplasmDiffusionTest {

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
    @DisplayName("vesicle diffusion - should move the correct distance")
    void shouldScaleDisplacementCorrectly() {
        // setup space
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(10, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        double simulationExtend = 1000;
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);
        UnitRegistry.setTime(Quantities.getQuantity(1, SECOND));

        // setup simulation
        Simulation simulation = new Simulation();

        // create vesicle
        Vector2D initialPosition = new Vector2D(simulationExtend / 2.0, simulationExtend / 2.0);
        Vesicle vesicle = new Vesicle(initialPosition, Quantities.getQuantity(50, NANO(METRE)));
        vesicle.setFeature(new PixelDiffusivity(Quantities.getQuantity(0.1, SQUARE_MICROMETRE_PER_SECOND)));

        // add vesicle transport layer
        VesicleLayer layer = new VesicleLayer(simulation);
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);

        // add vesicle diffusion
        VesicleCytoplasmDiffusion vesicleDiffusion = new VesicleCytoplasmDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);

        for (int i = 0; i < 100; i++) {
            ComparableQuantity<Time> previousTime = TimeStepManager.getElapsedTime();
            simulation.nextEpoch();
            ComparableQuantity<Time> currentTime = TimeStepManager.getElapsedTime();
            Vector2D currentPosition = simulation.getVesicleLayer().getVesicles().iterator().next().getPosition();
            double timeInSeconds = currentTime.subtract(previousTime).to(SECOND).getValue().doubleValue();
            double distanceInMicroMetre = UnitRegistry.scalePixelToSpace(initialPosition.subtract(currentPosition).getMagnitude()).to(MICRO(METRE)).getValue().doubleValue();
            double distancePerTime = distanceInMicroMetre / timeInSeconds;
            // variance is high because of random gaussian
            // maximal vector is 2,2 multiplied by sqrt 2, since diffusivity is 1
            assertTrue(5 > distancePerTime);
            assertTrue(distancePerTime > 0);
            initialPosition = currentPosition;
        }
    }
}