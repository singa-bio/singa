package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.EntityRegistry;
import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.core.utility.ListHelper;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import bio.singa.simulation.model.agents.linelike.LineLikeAgentLayer;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.modules.qualitative.implementations.LineLikeAgentAttachment;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.Arrays;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.features.ActinBoostVelocity.NANOMETRE_PER_SECOND;
import static bio.singa.simulation.features.MotorPullDirection.PLUS;
import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.ACTIN;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.ACTIN_ATTACHED;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.UNATTACHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.*;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class VesicleTransportTest {

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
    void testVesicleTransport() {
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(6, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(600);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);

        ComparableQuantity<Time> timeStep = Quantities.getQuantity(1, MILLI(SECOND));
        UnitRegistry.setTime(timeStep);

        ChemicalEntity myo = Protein.create("MYO").build();

        Simulation simulation = new Simulation();

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 1);
        simulation.setGraph(graph);

        Vector2D initialVesiclePosition = new Vector2D(50, 30);
        Vesicle vesicle = new Vesicle(initialVesiclePosition, Quantities.getQuantity(150, NANO(METRE)));
        vesicle.setState(UNATTACHED);
        vesicle.getConcentrationContainer().initialize(CellTopology.MEMBRANE,
                EntityRegistry.matchExactly("MYO"),
                Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));

        // add vesicle layer
        VesicleLayer layer = new VesicleLayer(simulation);
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);

        // membrane layer
        MembraneLayer membraneLayer = new MembraneLayer();

        // add filament layer
        ArrayList<Vector2D> track = new ArrayList<>();
        track.add(new Vector2D(50, 30));
        track.add(new Vector2D(150, 30));
        track.add(new Vector2D(200, 30));
        track.add(new Vector2D(250, 30));
        track.add(new Vector2D(300, 30));
        track.add(new Vector2D(350, 30));
        track.add(new Vector2D(400, 30));
        track.add(new Vector2D(500, 30));
        track.add(new Vector2D(550, 30));
        LineLikeAgent actinFilament = new LineLikeAgent(LineLikeAgent.ACTIN, track, NeumannRectangularDirection.EAST);
        LineLikeAgentLayer lineLikeAgentLayer = new LineLikeAgentLayer(simulation, membraneLayer);
        lineLikeAgentLayer.addFilament(actinFilament);
        simulation.setLineLayer(lineLikeAgentLayer);

        // 2b attachment to actin
        LineLikeAgentAttachment vesicleActinAttachment = new LineLikeAgentAttachment();
        vesicleActinAttachment.setIdentifier("attach vesicle to actin");
        vesicleActinAttachment.setFeature(new AttachedMotor(EntityRegistry.matchExactly("MYO")));
        vesicleActinAttachment.setFeature(new AttachedFilament(ACTIN));
        vesicleActinAttachment.setFeature(new MotorPullDirection(PLUS));
        vesicleActinAttachment.setFeature(new AttachmentDistance(Quantities.getQuantity(45, NANO(METRE))));
        simulation.addModule(vesicleActinAttachment);

        // 2c directed transport at actin
        VesicleTransport transportAtActinFilaments = new VesicleTransport();
        transportAtActinFilaments.setIdentifier("transport vesicle along actin filament");
        ComparableQuantity<Speed> speed = Quantities.getQuantity(600.0, NANOMETRE_PER_SECOND);
        transportAtActinFilaments.setFeature(new MotorMovementVelocity(speed));
        transportAtActinFilaments.setFeature(new AppliedVesicleState(ACTIN_ATTACHED));
        simulation.addModule(transportAtActinFilaments);

        simulation.nextEpoch();
        // first check attachment to vesicle module
        assertEquals(VesicleStateRegistry.ACTIN_ATTACHED, vesicle.getState());
        assertTrue(ListHelper.haveSameElements(Arrays.asList(graph.getNode(0, 0), graph.getNode(1, 0)),
                vesicle.getAssociatedNodes().keySet()));

        Quantity<Time> timeBefore = simulation.getElapsedTime();
        simulation.nextEpoch();
        Quantity<Time> timeAfter = simulation.getElapsedTime();

        // check distance travelled should be 600 nm/s
        Quantity<Length> distance = Environment.convertSimulationToSystemScale(vesicle.getPosition().subtract(initialVesiclePosition).getMagnitude()).to(NANO(METRE));
        Quantity<Time> duration = timeAfter.subtract(timeBefore).to(SECOND);
        assertEquals(speed.getValue().doubleValue(), distance.divide(duration).getValue().doubleValue(), 1e-10);

    }
}