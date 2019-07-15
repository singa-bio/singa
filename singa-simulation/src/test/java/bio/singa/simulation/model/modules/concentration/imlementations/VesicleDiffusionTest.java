package bio.singa.simulation.model.modules.concentration.imlementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.features.model.Evidence;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.MembraneDiffusion;
import bio.singa.simulation.model.modules.displacement.implementations.VesicleCytoplasmDiffusion;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tec.units.indriya.ComparableQuantity;
import tec.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import java.util.concurrent.ThreadLocalRandom;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.chemistry.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static bio.singa.simulation.model.sections.CellTopology.OUTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.units.indriya.unit.MetricPrefix.MICRO;
import static tec.units.indriya.unit.MetricPrefix.NANO;
import static tec.units.indriya.unit.Units.METRE;
import static tec.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class VesicleDiffusionTest {

    private static final ChemicalEntity water = SmallMolecule.create("water").build();

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
        Environment.setSystemExtend(Quantities.getQuantity(20, MICRO(METRE)));
        Environment.setSimulationExtend(500);
        UnitRegistry.setTime(Quantities.getQuantity(1, MICRO(SECOND)));

        Vesicle vesicle = new Vesicle(new Vector2D(50, 50), Quantities.getQuantity(100, NANO(METRE)));

        assertEquals(2.1460983910913096E-9, vesicle.getFeature(Diffusivity.class).getValue().doubleValue(), 1e-8);
        UnitRegistry.setTime(Quantities.getQuantity(2, MICRO(SECOND)));
        assertEquals(4.292196782182619E-9, vesicle.getFeature(Diffusivity.class).getValue().doubleValue(), 1e-8);
    }

    @Test
    void shouldMembraneDiffusionWithVesicles() {

        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(20, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(500);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);
        UnitRegistry.setTime(Quantities.getQuantity(1, MICRO(SECOND)));

        Simulation simulation = new Simulation();

        Vesicle vesicle = new Vesicle(new Vector2D(220, 220), Quantities.getQuantity(150, NANO(METRE)));

        vesicle.getConcentrationContainer().set(OUTER, water, 50.0);

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
        SmallMolecule water = SmallMolecule.create("water")
                .assignFeature(new MembranePermeability(Quantities.getQuantity(1.75e-3, CENTIMETRE_PER_SECOND), Evidence.NO_EVIDENCE))
                .assignFeature(new Diffusivity(Quantities.getQuantity(2.6e-6, SQUARE_CENTIMETRE_PER_SECOND), Evidence.NO_EVIDENCE))
                .build();

        // add diffusion
        MembraneDiffusion.inSimulation(simulation)
                .cargo(water)
                .build();

        VesicleCytoplasmDiffusion vesicleDiffusion = new VesicleCytoplasmDiffusion();
        vesicleDiffusion.setSimulation(simulation);
        simulation.getModules().add(vesicleDiffusion);

        for (int i = 0; i < 100; i++) {
            simulation.nextEpoch();
        }

    }


}
