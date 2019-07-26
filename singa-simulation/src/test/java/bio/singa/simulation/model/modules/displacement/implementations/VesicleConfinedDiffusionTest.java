package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Circles;
import bio.singa.mathematics.geometry.faces.ComplexPolygon;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.AppliedVesicleState;
import bio.singa.simulation.features.ContainmentRegion;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLayer;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;

import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.IN_PERINUCLEAR_STORAGE;
import static bio.singa.simulation.model.sections.CellRegions.PERINUCLEAR_REGION;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class VesicleConfinedDiffusionTest {

    @Test
    void testModuleInContext() {

        Simulation simulation = new Simulation();
        final double simulationExtend = 100;
        Vector2D centralPosition = new Vector2D(50.0, 50.0);
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        simulation.setSimulationRegion(rectangle);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(10, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        UnitRegistry.setTime(Quantities.getQuantity(100, MILLI(SECOND)));
        simulation.setMaximalTimeStep(Quantities.getQuantity(100, MILLI(SECOND)));

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(centralPosition);
        node.setCellRegion(CellRegions.CYTOPLASM_REGION);
        simulation.setGraph(graph);

        // setup volume for containment
        ComplexPolygon confinementArea = new ComplexPolygon(Circles.samplePoints(new Circle(centralPosition, 0.5), 10));
        VolumeLikeAgent containmentRegion = new VolumeLikeAgent(confinementArea, PERINUCLEAR_REGION);
        VolumeLayer volumeLayer = new VolumeLayer();
        volumeLayer.addAgent(containmentRegion);
        simulation.setVolumeLayer(volumeLayer);

        // setup vesicles
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle containedVesicle = new Vesicle(centralPosition, Quantities.getQuantity(100.0, NANO(METRE)));
        containedVesicle.setState(IN_PERINUCLEAR_STORAGE);
        vesicleLayer.addVesicle(containedVesicle);
        simulation.setVesicleLayer(vesicleLayer);

        // setup containment
        VesicleConfinedDiffusion confinedDiffusion = new VesicleConfinedDiffusion();
        confinedDiffusion.setIdentifier("prevent vesicles from leaving perinuclear region if in state IN_PERINUCLEAR_STORAGE");
        confinedDiffusion.setFeature(new ContainmentRegion(PERINUCLEAR_REGION));
        confinedDiffusion.setFeature(new AppliedVesicleState(IN_PERINUCLEAR_STORAGE));
        simulation.addModule(confinedDiffusion);

        // if vesicle leaves region at any point, fail
        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(100.0, SECOND))) {
            simulation.nextEpoch();
            assertTrue(containedVesicle.getPosition().isInside(confinementArea));
        }
        assertNotEquals(centralPosition, containedVesicle.getPosition());
    }

}