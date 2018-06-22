package de.bioforscher.singa.simulation.modules.newmodules.imlementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.diffusivity.Diffusivity;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import de.bioforscher.singa.simulation.model.layer.VesicleLayer;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.modules.newmodules.simulation.Simulation;
import de.bioforscher.singa.simulation.modules.transport.VesicleDiffusion;
import org.junit.After;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.newsections.CellTopology.INNER;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class VesicleDiffusionTest {

    private static final ChemicalEntity water = SmallMolecule.create("water").build();

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    public void shouldTransformConcentration() {
        Environment.setSystemExtend(Quantities.getQuantity(20, MICRO(METRE)));
        Environment.setSimulationExtend(500);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(50, 50),
                Quantities.getQuantity(ThreadLocalRandom.current().nextDouble(100, 200), NANO(METRE)));

        ComparableQuantity<MolarConcentration> originalQuantity = Quantities.getQuantity(10.0, MOLE_PER_LITRE);
        vesicle.setConcentration(water, originalQuantity);
        Quantity<MolarConcentration> concentration = vesicle.getConcentration(water);
        Quantity<MolarConcentration> transformedQuantity = concentration.to(MOLE_PER_LITRE);
        assertEquals(originalQuantity.getValue().doubleValue(), transformedQuantity.getValue().doubleValue(), 1e-8);
    }

    @Test
    public void shouldRescaleDiffusivity() {
        Environment.setSystemExtend(Quantities.getQuantity(20, MICRO(METRE)));
        Environment.setSimulationExtend(500);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(50, 50),
                Quantities.getQuantity(100, NANO(METRE)));

        vesicle.scaleScalableFeatures();
        System.out.println(vesicle.getFeature(Diffusivity.class));
        assertEquals(2.1460983910913096E-9, vesicle.getFeature(Diffusivity.class).getValue().doubleValue(), 1e-8);
        Environment.setTimeStep(Quantities.getQuantity(2, MICRO(SECOND)));
        vesicle.scaleScalableFeatures();
        assertEquals(4.292196782182619E-9, vesicle.getFeature(Diffusivity.class).getValue().doubleValue(), 1e-8);
    }

    @Test
    public void shouldSimulateVesicleDiffusion() {
        double simulationExtend = 800;
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(20, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(500);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        Simulation simulation = new Simulation();

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(220, 220),
                Quantities.getQuantity(ThreadLocalRandom.current().nextDouble(100, 200), NANO(METRE))
                        .to(Environment.getNodeDistance().getUnit()));

        vesicle.getConcentrationContainer().set(INNER, water, 50.0);

        // add vesicle transport layer
        VesicleLayer layer = new VesicleLayer();
        layer.addVesicle(vesicle);
        simulation.setVesicleLayer(layer);

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);

        for (AutomatonNode node : graph.getNodes()) {
            node.setCellRegion(CellRegion.CYTOSOL_A);
            node.getConcentrationContainer().set(INNER, water, 40.0);
        }

        // setup species
        SmallMolecule water = new SmallMolecule.Builder("water")
                .name("water")
                .assignFeature(new MembranePermeability(Quantities.getQuantity(0.1, CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .assignFeature(new Diffusivity(10, FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        // add diffusion
        MembraneDiffusion.inSimulation(simulation)
                .cargo(water)
                .build();

//        Diffusion.inSimulation(simulation)
//                .onlyFor(water)
//                .build();

        VesicleDiffusion vesicleDiffusion = new VesicleDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }

        // FIXME seems like something is not reset correctly, error changes for the same calculation but time step and delta don't change
//        15:40:16 DEBUG [ConcentrationBasedModule:determineLargestLocalError:250] - The largest error was 0.010517200761954837 for Vesicle{radius=0.17851068340352283 µm, position=Vector 2D (220.35791294623274, 219.81862432174586)}
//        15:40:16 TRACE [ConcentrationBasedModule:evaluateModuleState:280] - Recalculation required for error 0.010517200761954837.
//        15:40:16 DEBUG [UpdateScheduler:processModuleByState:76] - MembraneDiffusion (water) is REQUIRING_RECALCULATION
//        15:40:16 DEBUG [UpdateScheduler:decreaseTimeStep:149] - Decreasing time step to 3.919284520776990226398735931284693 µs.
//        15:40:16 TRACE [ConcentrationBasedModule:logDelta:165] - Full delta for water in Vesicle 0:V0I = -9.56897593902488E-18 mol/µm³
//        15:40:16 TRACE [ConcentrationBasedModule:logDelta:165] - Full delta for water in Node (4, 4):SA = 9.56897593902488E-18 mol/µm³
//        15:40:16 TRACE [ConcentrationBasedModule:logDelta:165] - Half delta for water in Vesicle 0:V0I = -4.8315877479966114E-18 mol/µm³
//        15:40:16 TRACE [ConcentrationBasedModule:logDelta:165] - Half delta for water in Node (4, 4):SA = 4.8315877479966114E-18 mol/µm³
//        15:40:16 DEBUG [ConcentrationBasedModule:determineLargestLocalError:250] - The largest error was 0.009748302409223752 for Vesicle{radius=0.17851068340352283 µm, position=Vector 2D (220.35791294623274, 219.81862432174586)}
//        15:40:16 DEBUG [ConcentrationBasedModule:optimizeTimeStep:273] - Optimized local error for MembraneDiffusion (water) was 0.009748302409223752 with time step of 3.919284520776990226398735931284693 µs.
//        15:40:16 DEBUG [UpdateScheduler:processModuleByState:76] - MembraneDiffusion (water) is PENDING
//        15:40:16 TRACE [ConcentrationBasedModule:logDelta:165] - Full delta for water in Vesicle 0:V0I = -9.56897593902488E-18 mol/µm³
//        15:40:16 TRACE [ConcentrationBasedModule:logDelta:165] - Full delta for water in Node (4, 4):SA = 9.56897593902488E-18 mol/µm³
//        15:40:16 TRACE [ConcentrationBasedModule:logDelta:165] - Half delta for water in Vesicle 0:V0I = -4.827833261447305E-18 mol/µm³
//        15:40:16 TRACE [ConcentrationBasedModule:logDelta:165] - Half delta for water in Node (4, 4):SA = 4.827833261447305E-18 mol/µm³
//        15:40:16 DEBUG [ConcentrationBasedModule:determineLargestLocalError:250] - The largest error was 0.00897820815002015 for Vesicle{radius=0.17851068340352283 µm, position=Vector 2D (220.35791294623274, 219.81862432174586)}
//        15:40:16 DEBUG [UpdateScheduler:processModuleByState:76] - MembraneDiffusion (water) is SUCCEEDED

    }


}
