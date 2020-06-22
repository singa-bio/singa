package bio.singa.simulation.model.modules.displacement.implementations;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.simple.Protein;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.ActinBoostVelocity;
import bio.singa.simulation.features.BoostMediatingEntity;
import bio.singa.simulation.model.agents.linelike.MicrotubuleOrganizingCentre;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.error.TimeStepManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import javax.measure.quantity.Time;

import static bio.singa.simulation.entities.EntityRegistry.matchExactly;
import static bio.singa.simulation.features.ActinBoostVelocity.NANOMETRE_PER_SECOND;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.ACTIN_PROPELLED;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class EndocytosisActinBoostTest {

    @Test
    @DisplayName("actin boost speed")
    void testActinBoost() {
        double simulationExtend = 1000;
        int nodesHorizontal = 5;

        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        Simulation simulation = new Simulation();
        simulation.setSimulationRegion(rectangle);

        // setup scaling
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(1000, NANO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, nodesHorizontal);
        UnitRegistry.setTime(Quantities.getQuantity(1, SECOND));

        // setup graph and assign regions
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(nodesHorizontal, 1);
        simulation.setGraph(graph);

        ChemicalEntity boostMediator = Protein.create("BOOST_MEDIATOR")
                .membraneBound()
                .build();

        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        Vector2D initialPosition = new Vector2D(50, 50);
        Vesicle vesicle = new Vesicle(initialPosition, Quantities.getQuantity(50, NANO(METRE)));
        vesicle.setState(ACTIN_PROPELLED);
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        MembraneLayer membraneLayer = new MembraneLayer();
        MicrotubuleOrganizingCentre moc = new MicrotubuleOrganizingCentre(membraneLayer, new Circle(new Vector2D(450, 50), 30));
        membraneLayer.setMicrotubuleOrganizingCentre(moc);
        simulation.setMembraneLayer(membraneLayer);

        ConcentrationBuilder.create(simulation)
                .entity(boostMediator)
                .topology(MEMBRANE)
                .molecules(1)
                .onlyVesicles()
                .build();

        EndocytosisActinBoost boost = new EndocytosisActinBoost();
        boost.setIdentifier("vesicle boost");
        boost.setFeature(new BoostMediatingEntity(matchExactly("BOOST_MEDIATOR")));
        boost.setFeature(new ActinBoostVelocity(Quantities.getQuantity(50.0, NANOMETRE_PER_SECOND)));

        simulation.addModule(boost);

        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            ComparableQuantity<Time> previousTime = TimeStepManager.getElapsedTime();
            simulation.nextEpoch();
            ComparableQuantity<Time> currentTime = TimeStepManager.getElapsedTime();
            Vector2D currentPosition = simulation.getVesicleLayer().getVesicles().iterator().next().getPosition();
            double distancePerSecond = UnitRegistry.scalePixelToSpace(initialPosition.subtract(currentPosition).getMagnitude()).to(NANO(METRE)).getValue().doubleValue() / currentTime.subtract(previousTime).to(SECOND).getValue().doubleValue();
            // variance is high because of random gaussian
            assertEquals(50, distancePerSecond, 1e-5);
            initialPosition = currentPosition;
        }

    }
}