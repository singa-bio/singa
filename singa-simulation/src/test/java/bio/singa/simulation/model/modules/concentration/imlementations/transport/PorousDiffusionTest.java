package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.features.AffectedRegion;
import bio.singa.simulation.features.Cargo;
import bio.singa.simulation.features.MembraneTickness;
import bio.singa.simulation.features.Ratio;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import java.util.List;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
class PorousDiffusionTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
        Environment.setSimulationExtend(1000);
        Environment.setSystemExtend(Quantities.getQuantity(1, MICRO(METRE)));
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    void testPorousDiffusion() {

        Simulation simulation = new Simulation();

        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        simulation.setGraph(graph);
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CELL_OUTER_MEMBRANE_REGION);

        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        simulation.setGraph(graph);

        ChemicalEntity camp = SmallMolecule.create("CAMP")
                .assignFeature(new Diffusivity(Quantities.getQuantity(32, new ProductUnit<>(MICRO(METRE).pow(2).divide(SECOND)).asType(Diffusivity.class))))
                .build();

        PorousDiffusion.inSimulation(simulation)
                .cargo(new Cargo(camp))
                .region(new AffectedRegion(CELL_OUTER_MEMBRANE_REGION))
                .membraneThickness(new MembraneTickness(Quantities.getQuantity(100, NANO(METRE))))
                .poreMembraneRatio(new Ratio(0.5))
                .identifier("porous diffusion")
                .build();

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(CELL_OUTER_MEMBRANE_REGION.getOuterSubsection(), camp, Quantities.getQuantity(1.0, MICRO_MOLE_PER_LITRE));
        simulation.setConcentrationInitializer(ci);

        double previousOuterConcentration = UnitRegistry.convert(Quantities.getQuantity(1.0, MICRO_MOLE_PER_LITRE)).getValue().doubleValue();
        double previousInnerConcentration = 0.0;
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            // outer assertions
            double currentOuterConcentration = node.getConcentrationContainer().get(CellTopology.OUTER, camp);
            assertTrue(currentOuterConcentration < previousOuterConcentration);
            previousOuterConcentration = currentOuterConcentration;
            // inner assertions
            double currentInnerConcentration = node.getConcentrationContainer().get(CellTopology.INNER, camp);
            assertTrue(currentInnerConcentration > previousInnerConcentration);
            previousInnerConcentration = currentInnerConcentration;
        }

    }
}