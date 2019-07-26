package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.AppliedVesicleState;
import bio.singa.simulation.features.BlackListVesicleStates;
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
import java.util.Arrays;

import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class VolumeLikeAgentContainmentTest {

    @Test
    void testModuleInContext() {

        // setup simulation
        Simulation simulation = new Simulation();
        final double simulationExtend = 100;
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        simulation.setSimulationRegion(rectangle);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(10, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        UnitRegistry.setTime(Quantities.getQuantity(1, MILLI(SECOND)));

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegions.CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(graph);

        // setup volume for containment
        VolumeLikeAgent containmentRegion = new VolumeLikeAgent(new Rectangle(50, 100), CellRegions.PERINUCLEAR_REGION);
        VolumeLayer volumeLayer = new VolumeLayer();
        volumeLayer.addAgent(containmentRegion);
        simulation.setVolumeLayer(volumeLayer);

        // setup vesicles
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vesicle containedVesicle = new Vesicle(new Vector2D(25.0, 50.0), Quantities.getQuantity(100.0, NANO(METRE)));
        Vesicle uncontainedVesicle = new Vesicle(new Vector2D(75.0, 50.0), Quantities.getQuantity(100.0, NANO(METRE)));
        vesicleLayer.addVesicle(containedVesicle);
        vesicleLayer.addVesicle(uncontainedVesicle);
        simulation.setVesicleLayer(vesicleLayer);

        // setup containment
        VolumeLikeAgentContainment perinuclearContainment = new VolumeLikeAgentContainment();
        perinuclearContainment.setIdentifier("confine tethered diffusion to perinuclear region");
        perinuclearContainment.setFeature(new ContainmentRegion(CellRegions.PERINUCLEAR_REGION));
        perinuclearContainment.setFeature(new BlackListVesicleStates(Arrays.asList(IN_PERINUCLEAR_STORAGE, ACTIN_PROPELLED)));
        perinuclearContainment.setFeature(new AppliedVesicleState(IN_PERINUCLEAR_STORAGE));
        simulation.addModule(perinuclearContainment);

        simulation.nextEpoch();

        assertEquals(IN_PERINUCLEAR_STORAGE, containedVesicle.getState());
        assertEquals(UNATTACHED, uncontainedVesicle.getState());

    }

}