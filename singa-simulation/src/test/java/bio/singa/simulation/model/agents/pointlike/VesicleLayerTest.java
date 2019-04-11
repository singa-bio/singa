package bio.singa.simulation.model.agents.pointlike;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tec.units.indriya.ComparableQuantity;
import tec.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tec.units.indriya.unit.MetricPrefix.MICRO;
import static tec.units.indriya.unit.MetricPrefix.NANO;
import static tec.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
class VesicleLayerTest {

    @Test
    void testCorrectSubsectionAssignment() {

        double simulationExtend = 150;
        int nodesHorizontal = 3;
        int nodesVertical = 3;

        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        Simulation simulation = new Simulation();
        simulation.setSimulationRegion(rectangle);

        // setup scaling
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(1, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);

        // setup graph and assign regions
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, nodesVertical);
        simulation.setGraph(graph);

        // initialize vesicle layer
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        simulation.setVesicleLayer(vesicleLayer);

        ComparableQuantity<Length> radius = Quantities.getQuantity(20, NANO(METRE));
        // vesicle contained
        Vesicle v1 = new Vesicle(new Vector2D(25.0,25.0), radius);
        // vesicle halved
        Vesicle v2 = new Vesicle(new Vector2D(25.0,50.0), radius);
        // vesicle quartered
        Vesicle vSE = new Vesicle(new Vector2D(50.1,50.1), radius);
        Vesicle vSW = new Vesicle(new Vector2D(99.9,50.1), radius);
        Vesicle vNE = new Vesicle(new Vector2D(50.1,99.9), radius);
        Vesicle vNW = new Vesicle(new Vector2D(99.9,99.9), radius);

        vesicleLayer.addVesicle(v1);
        vesicleLayer.addVesicle(v2);
        vesicleLayer.addVesicle(vSE);
        vesicleLayer.addVesicle(vSW);
        vesicleLayer.addVesicle(vNE);
        vesicleLayer.addVesicle(vNW);

        vesicleLayer.associateVesicles();

        Set<RectangularCoordinate> coordinatesSE = vSE.getAssociatedNodes().keySet().stream()
                .map(AutomatonNode::getIdentifier)
                .collect(Collectors.toSet());

        assertTrue(coordinatesSE.contains(new RectangularCoordinate(1,1)));
        assertTrue(coordinatesSE.contains(new RectangularCoordinate(0,0)));
        assertTrue(coordinatesSE.contains(new RectangularCoordinate(0,1)));
        assertTrue(coordinatesSE.contains(new RectangularCoordinate(1,0)));

        Set<RectangularCoordinate> coordinatesSW = vSW.getAssociatedNodes().keySet().stream()
                .map(AutomatonNode::getIdentifier)
                .collect(Collectors.toSet());

        assertTrue(coordinatesSW.contains(new RectangularCoordinate(2,1)));
        assertTrue(coordinatesSW.contains(new RectangularCoordinate(1,0)));
        assertTrue(coordinatesSW.contains(new RectangularCoordinate(1,1)));
        assertTrue(coordinatesSW.contains(new RectangularCoordinate(2,0)));

        Set<RectangularCoordinate> coordinatesNE = vNE.getAssociatedNodes().keySet().stream()
                .map(AutomatonNode::getIdentifier)
                .collect(Collectors.toSet());

        assertTrue(coordinatesNE.contains(new RectangularCoordinate(1,1)));
        assertTrue(coordinatesNE.contains(new RectangularCoordinate(1,2)));
        assertTrue(coordinatesNE.contains(new RectangularCoordinate(0,1)));
        assertTrue(coordinatesNE.contains(new RectangularCoordinate(0,2)));

        Set<RectangularCoordinate> coordinatesNW = vNW.getAssociatedNodes().keySet().stream()
                .map(AutomatonNode::getIdentifier)
                .collect(Collectors.toSet());

        assertTrue(coordinatesNW.contains(new RectangularCoordinate(2,1)));
        assertTrue(coordinatesNW.contains(new RectangularCoordinate(2,2)));
        assertTrue(coordinatesNW.contains(new RectangularCoordinate(1,1)));
        assertTrue(coordinatesNW.contains(new RectangularCoordinate(1,2)));

    }
}