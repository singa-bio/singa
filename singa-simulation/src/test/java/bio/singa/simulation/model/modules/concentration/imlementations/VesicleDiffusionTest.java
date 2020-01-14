package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.PixelDiffusivity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleCytoplasmDiffusion;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.concurrent.ThreadLocalRandom;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_MICROMETRE_PER_SECOND;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellTopology.OUTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class VesicleDiffusionTest {

    private static final ChemicalEntity water = SmallMolecule.create("water").build();

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    void shouldTransformConcentration() {
        Environment.setSystemExtend(Quantities.getQuantity(20, MICRO(METRE)));
        Environment.setSimulationExtend(500);
        UnitRegistry.setTime(Quantities.getQuantity(1, MICRO(SECOND)));

        Vesicle vesicle = new Vesicle(
                new Vector2D(50, 50),
                Quantities.getQuantity(ThreadLocalRandom.current().nextDouble(100, 200), NANO(METRE)));

        ComparableQuantity<MolarConcentration> originalQuantity = Quantities.getQuantity(10.0, MOLE_PER_LITRE);
        vesicle.getConcentrationContainer().initialize(OUTER, water, originalQuantity);
        double concentration = vesicle.getConcentrationContainer().get(OUTER, water);
        double transformedQuantity = UnitRegistry.concentration(concentration).to(MOLE_PER_LITRE).getValue().doubleValue();
        assertEquals(originalQuantity.getValue().doubleValue(), transformedQuantity, 1e-8);
    }

    @Test
    void shouldRescaleDiffusivity() {
        Environment.setSystemExtend(Quantities.getQuantity(10, MICRO(METRE)));
        Environment.setSimulationExtend(100);
        Environment.setNodeSpacingToDiameter(Quantities.getQuantity(10, MICRO(METRE)), 10);

        Vesicle vesicle = new Vesicle(new Vector2D(50, 50), Quantities.getQuantity(100, NANO(METRE)));

        assertEquals(0.015, vesicle.getFeature(PixelDiffusivity.class).getContent().getValue().doubleValue());
        UnitRegistry.setTime(Quantities.getQuantity(1, MICRO(SECOND)));
        assertEquals(1.5e-8, vesicle.getFeature(PixelDiffusivity.class).getScaledQuantity());
        UnitRegistry.setTime(Quantities.getQuantity(2, MICRO(SECOND)));
        assertEquals(3.0e-8, vesicle.getFeature(PixelDiffusivity.class).getScaledQuantity());
    }

    @Test
    void shouldMembraneDiffusionWithVesicles() {

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
            System.out.println(i);
            ComparableQuantity<Time> previousTime = simulation.getElapsedTime();
            simulation.nextEpoch();
            ComparableQuantity<Time> currentTime = simulation.getElapsedTime();
            Vector2D currentPosition = simulation.getVesicleLayer().getVesicles().iterator().next().getPosition();
            double timeInSeconds = currentTime.subtract(previousTime).to(SECOND).getValue().doubleValue();
            double distanceInMicroMetre = UnitRegistry.scalePixelToSpace(initialPosition.subtract(currentPosition).getMagnitude()).to(MICRO(METRE)).getValue().doubleValue();
            // System.out.println(distanceInMicroMetre + " um in" + timeInSeconds + " s");
            System.out.println(distanceInMicroMetre/timeInSeconds+" um/s");
            // variance is high because of random gaussian
            // assertEquals(50, distancePerSecond, 1e-5);
            initialPosition = currentPosition;
        }

    }


}
