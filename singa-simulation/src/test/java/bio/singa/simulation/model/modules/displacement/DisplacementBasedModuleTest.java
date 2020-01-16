package bio.singa.simulation.model.modules.displacement;

import bio.singa.chemistry.features.diffusivity.PixelDiffusivity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleCytoplasmDiffusion;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_MICROMETRE_PER_SECOND;
import static org.junit.jupiter.api.Assertions.*;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class DisplacementBasedModuleTest {

    @Test
    @DisplayName("displacement based modules - error scaling")
    void testErrorScaling() {
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
        vesicle.setFeature(new PixelDiffusivity(Quantities.getQuantity(1, SQUARE_MICROMETRE_PER_SECOND)));

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

        for (int i = 0; i < 10; i++) {
            ComparableQuantity<Time> previousTime = simulation.getElapsedTime();
            simulation.nextEpoch();
            ComparableQuantity<Time> currentTime = simulation.getElapsedTime();
            Vector2D currentPosition = simulation.getVesicleLayer().getVesicles().iterator().next().getPosition();
            double timeInSeconds = currentTime.subtract(previousTime).to(SECOND).getValue().doubleValue();
            double distanceInMicroMetre = UnitRegistry.scalePixelToSpace(initialPosition.subtract(currentPosition).getMagnitude()).to(MICRO(METRE)).getValue().doubleValue();
            double distancePerTime = distanceInMicroMetre / timeInSeconds;

            initialPosition = currentPosition;
        }
    }
}