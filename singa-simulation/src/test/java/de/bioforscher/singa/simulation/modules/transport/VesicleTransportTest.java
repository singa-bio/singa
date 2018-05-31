package de.bioforscher.singa.simulation.modules.transport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.SmallMolecule;
import de.bioforscher.singa.chemistry.descriptive.features.permeability.MembranePermeability;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import de.bioforscher.singa.simulation.model.layer.VesicleLayer;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.modules.model.Simulation;
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
public class VesicleTransportTest {

    private static final ChemicalEntity water = SmallMolecule.create("water").build();

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

        Environment.reset();
    }

    @Test
    public void shouldDiffuseFromVesicle() {

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(20, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(500);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);
        Environment.setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        Simulation simulation = new Simulation();

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(25, 51),
                Quantities.getQuantity(ThreadLocalRandom.current()
                        .nextDouble(100, 200), NANO(METRE))
                        .to(Environment.getNodeDistance().getUnit()));

        vesicle.getConcentrationContainer().set(INNER, water, 50.0);

        // add vesicle transport layer
        VesicleLayer vesicleLayer = new VesicleLayer();
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

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
                .assignFeature(new MembranePermeability(Quantities.getQuantity(3.5E-03, CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
                .build();

        // add diffusion
        MembraneDiffusion.inSimulation(simulation)
                .cargo(water)
                .build();

        vesicleLayer.addVesicleModule(new VesicleDiffusion(simulation));

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

        Environment.reset();
    }

}
