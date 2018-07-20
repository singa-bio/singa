package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.modules.displacement.VesicleLayer;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Length;

import static org.junit.Assert.assertNotEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class VesicleDiffusionTest {

    @Test
    public void testVesicleDiffusion() {

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(20, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(500);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        Simulation simulation = new Simulation();

        Vector2D previousPosition = new Vector2D(220, 220);
        Vesicle vesicle = new Vesicle("0",
                previousPosition,
                Quantities.getQuantity(150, NANO(METRE))
                        .to(Environment.getNodeDistance().getUnit()));

        // add vesicle transport layer
        VesicleLayer layer = new VesicleLayer(simulation);
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);

        // diffusion module
        VesicleDiffusion vesicleDiffusion = new VesicleDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);
        simulation.initializeSpatialRepresentations();

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            Vector2D currentPosition = simulation.getVesicleLayer().getVesicles().get(0).getCurrentPosition();
            assertNotEquals(currentPosition, previousPosition);
            previousPosition = currentPosition;
        }
    }


}