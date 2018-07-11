package de.bioforscher.singa.simulation.model.modules.concentration.imlementations;

import de.bioforscher.singa.chemistry.entities.SmallMolecule;
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
import de.bioforscher.singa.simulation.model.sections.CellTopology;
import de.bioforscher.singa.simulation.model.simulation.Simulation;
import org.junit.After;
import org.junit.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.chemistry.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static de.bioforscher.singa.features.parameters.Environment.*;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.sections.CellRegion.CYTOSOL_A;
import static de.bioforscher.singa.simulation.model.sections.CellRegion.MEMBRANE;
import static de.bioforscher.singa.simulation.model.sections.CellTopology.INNER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class MembraneDiffusionTest {

    private SmallMolecule water = new SmallMolecule.Builder("water")
            .name("water")
            .assignFeature(new MembranePermeability(Quantities.getQuantity(3.5E-03, CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED))
            .build();

    @After
    public void cleanUp() {
        Environment.reset();
    }

    @Test
    public void shouldSimulateMembraneDiffusion() {
        Environment.reset();
        Simulation simulation = new Simulation();

        final AutomatonGraph automatonGraph = AutomatonGraphs.singularGraph();
        simulation.setGraph(automatonGraph);

        AutomatonNode membraneNode = automatonGraph.getNode(0, 0);
        membraneNode.setCellRegion(MEMBRANE);
        membraneNode.setAvailableConcentration(MEMBRANE.getInnerSubsection(), water, Quantities.getQuantity(2.0, MOLE_PER_LITRE).to(getConcentrationUnit()));
        membraneNode.setAvailableConcentration(MEMBRANE.getOuterSubsection(), water, Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(getConcentrationUnit()));
        automatonGraph.addNode(membraneNode);

        MembraneDiffusion.inSimulation(simulation)
                .cargo(water)
                .build();

        simulation.nextEpoch();
        // delta should be about 3.5e-20 mol/um3
        ComparableQuantity<MolarConcentration> expectedLeft = Quantities.getQuantity(2.0, MOLE_PER_LITRE).to(getConcentrationUnit()).subtract(Quantities.getQuantity(3.5e-20, getConcentrationUnit()));
        ComparableQuantity<MolarConcentration> expectedRight = Quantities.getQuantity(1.0, MOLE_PER_LITRE).to(getConcentrationUnit()).add(Quantities.getQuantity(3.5e-20, getConcentrationUnit()));
        assertEquals(expectedLeft.getValue().doubleValue(), membraneNode.getConcentration(MEMBRANE.getInnerSubsection(), water).getValue().doubleValue(), 1e-23);
        assertEquals(expectedRight.getValue().doubleValue(), membraneNode.getConcentration(MEMBRANE.getOuterSubsection(), water).getValue().doubleValue(), 1e-23);
    }

    @Test
    public void testConversionOfArea() {

        setNodeDistance(Quantities.getQuantity(2, MICRO(METRE)));
        setTimeStep(Quantities.getQuantity(1, MICRO(SECOND)));

        MembranePermeability membranePermeability = new MembranePermeability(Quantities.getQuantity(3.5E-03, CENTIMETRE_PER_SECOND), FeatureOrigin.MANUALLY_ANNOTATED);
        membranePermeability.scale();
        Quantity<MembranePermeability> scaledQuantity = membranePermeability.getScaledQuantity();
        ProductUnit<MolarConcentration> unit = new ProductUnit<>(Units.MOLE.divide(Environment.getSubsectionVolume().getUnit()));
        Quantity<MolarConcentration> concentration = Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(unit);

        double result = scaledQuantity.getValue().doubleValue() * concentration.getValue().doubleValue() * Environment.getSubsectionArea().getValue().doubleValue();

        assertEquals(1.4E-5, Quantities.getQuantity(result, unit).to(MOLE_PER_LITRE).getValue().doubleValue(), 1.0E-16);

    }

    @Test
    public void shouldDiffuseFromVesicle() {

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(20, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(500);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);

        Simulation simulation = new Simulation();

        Vesicle vesicle = new Vesicle("0",
                new Vector2D(20, 20),
                Quantities.getQuantity(ThreadLocalRandom.current()
                        .nextDouble(100, 200), NANO(METRE))
                        .to(Environment.getNodeDistance().getUnit()));

        vesicle.getConcentrationContainer().set(INNER, water, 50.0);

        // add vesicle transport layer
        VesicleLayer vesicleLayer = new VesicleLayer(simulation);
        vesicleLayer.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicleLayer);

        // define graphs
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(10, 10);
        simulation.setGraph(graph);

        for (AutomatonNode node : graph.getNodes()) {
            node.setCellRegion(CYTOSOL_A);
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

        // vesicleLayer.addVesicleModule(new VesicleDiffusion(simulation));
        // simulate a couple of epochs
        AutomatonNode node = graph.getNode(0,0);
        Quantity<MolarConcentration> previousVesicleConcentration = null;
        Quantity<MolarConcentration> previousNodeConcentration = null;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // node increasing
            Quantity<MolarConcentration> currentNodeConcentration = node.getConcentrationContainer().get(CellTopology.INNER, water).to(MOLE_PER_LITRE);
            if (previousNodeConcentration != null) {
                assertTrue(currentNodeConcentration.getValue().doubleValue() > previousNodeConcentration.getValue().doubleValue());
            }
            previousNodeConcentration = currentNodeConcentration;
            // vesicle decreasing
            Quantity<MolarConcentration> currentVesicleConcentration = vesicle.getConcentrationContainer().get(CellTopology.INNER, water).to(MOLE_PER_LITRE);
            if (previousVesicleConcentration != null) {
                assertTrue(currentVesicleConcentration.getValue().doubleValue() < previousVesicleConcentration.getValue().doubleValue());
            }
            previousVesicleConcentration = currentVesicleConcentration;
        }
    }

}