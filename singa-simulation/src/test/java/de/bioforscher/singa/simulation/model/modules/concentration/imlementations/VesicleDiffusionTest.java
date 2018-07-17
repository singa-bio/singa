package de.bioforscher.singa.simulation.model.modules.concentration.imlementations;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.features.diffusivity.Diffusivity;
import de.bioforscher.singa.chemistry.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.modules.displacement.VesicleLayer;
import de.bioforscher.singa.simulation.model.modules.displacement.implementations.VesicleDiffusion;
import de.bioforscher.singa.simulation.model.sections.CellRegion;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import org.junit.After;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.chemistry.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.sections.CellTopology.INNER;
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
        assertEquals(2.1460983910913096E-9, vesicle.getFeature(Diffusivity.class).getValue().doubleValue(), 1e-8);
        Environment.setTimeStep(Quantities.getQuantity(2, MICRO(SECOND)));
        vesicle.scaleScalableFeatures();
        assertEquals(4.292196782182619E-9, vesicle.getFeature(Diffusivity.class).getValue().doubleValue(), 1e-8);
    }

    @Test
    public void shouldMembraneDiffusionWithVesicles() {

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(20, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(500);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        Simulation simulation = new Simulation();

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(220, 220),
                Quantities.getQuantity(150, NANO(METRE))
                        .to(Environment.getNodeDistance().getUnit()));

        vesicle.getConcentrationContainer().set(INNER, water, 50.0);

        // add vesicle transport layer
        VesicleLayer layer = new VesicleLayer(simulation);
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
                .assignFeature(new MembranePermeability(Quantities.getQuantity(1.75e-3, CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .assignFeature(new Diffusivity(2.6e-6, FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        // add diffusion
        MembraneDiffusion.inSimulation(simulation)
                .cargo(water)
                .build();

        VesicleDiffusion vesicleDiffusion = new VesicleDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }

    }


}
