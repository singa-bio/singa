package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.features.quantities.ConcentrationDiffusivity;
import bio.singa.features.quantities.Diffusivity;
import bio.singa.core.utility.Pair;
import bio.singa.core.utility.Resources;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.features.AffectedRegion;
import bio.singa.simulation.features.Cargo;
import bio.singa.simulation.features.MembraneTickness;
import bio.singa.simulation.features.Ratio;
import bio.singa.simulation.model.agents.surfacelike.GridImageReader;
import bio.singa.simulation.model.agents.surfacelike.GridMembraneBuilder;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.ProductUnit;

import javax.measure.quantity.Length;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.*;
import static bio.singa.simulation.model.sections.CellSubsections.EXTRACELLULAR_REGION;
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
    @Disabled("migration to two compartment model")
    void testPorousDiffusion() {

        Simulation simulation = new Simulation();

        GridImageReader imageReader = GridImageReader.readTemplate(Resources.getResourceAsFileLocation("grids/membrane_10x2.png"));
        int[][] sectionArray = imageReader.getGrid();

        Environment.setSimulationExtend(1000);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(1000, NANO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setNodeSpacingToDiameter(systemExtend, 10);

        GridMembraneBuilder gmb = new GridMembraneBuilder(sectionArray);
        int red = Color.RED.getRGB();
        int blue = Color.BLUE.getRGB();
        Map<Integer, CellRegion> subsectionMap = new HashMap<>();
        subsectionMap.put(red, CYTOPLASM_REGION);
        subsectionMap.put(blue, CellRegions.EXTRACELLULAR_REGION);

        Map<Pair<Integer>, CellRegion> membraneMap = new HashMap<>();
        membraneMap.put(new Pair<>(blue, red), CELL_OUTER_MEMBRANE_REGION);
        membraneMap.put(new Pair<>(red, blue), CELL_INNER_MEMBRANE_REGION);

        gmb.setRegionMapping(subsectionMap);
        gmb.setMembraneMapping(membraneMap);
        gmb.createTopology();

        AutomatonGraph graph = gmb.getGraph();
        simulation.setGraph(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        Membrane membrane = gmb.getMembranes().get(CELL_OUTER_MEMBRANE_REGION);
        membraneLayer.addMembrane(membrane);
        simulation.setMembraneLayer(membraneLayer);

        simulation.setGraph(graph);

        ChemicalEntity camp = SmallMolecule.create("CAMP")
                .assignFeature(new ConcentrationDiffusivity(Quantities.getQuantity(32, new ProductUnit<>(MICRO(METRE).pow(2).divide(SECOND)).asType(Diffusivity.class))))
                .build();

        PorousDiffusion.inSimulation(simulation)
                .cargo(new Cargo(camp))
                .region(new AffectedRegion(CELL_OUTER_MEMBRANE_REGION))
                .membraneThickness(new MembraneTickness(Quantities.getQuantity(100, NANO(METRE))))
                .poreMembraneRatio(new Ratio(0.5))
                .identifier("porous diffusion")
                .build();

        ConcentrationBuilder.create(simulation)
                .entity(camp)
                .subsection(EXTRACELLULAR_REGION)
                .concentrationValue(1.0)
                .microMolar()
                .build();

        AutomatonNode node = simulation.getGraph().getNode(0, 0);
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