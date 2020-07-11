package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.features.radius.Radius;
import bio.singa.core.utility.Pair;
import bio.singa.core.utility.Resources;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.SimpleEntity;
import bio.singa.simulation.model.agents.surfacelike.GridImageReader;
import bio.singa.simulation.model.agents.surfacelike.GridMembraneBuilder;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.concentrations.ConcentrationBuilder;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.*;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.quantity.Length;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.*;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
class LateralMembraneDiffusionTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @BeforeEach
    void initializeEach() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    @DisplayName("lateral diffusion in membranes")
    void shouldDiffuseInMembrane() {

        // create simulation
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
        subsectionMap.put(blue, EXTRACELLULAR_REGION);

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

        ChemicalEntity lacz = SimpleEntity.create("LacZ")
                .assignFeature(Radius
                        .of(3.0, NANO(METRE))
                        .build())
                .build();

        // set concentrations
        ConcentrationBuilder.create(simulation)
                .entity(lacz)
                .topology(MEMBRANE)
                .concentrationValue(10)
                .microMolar()
                .updatableIdentifier("n(5,1)")
                .build();

        LateralMembraneDiffusion.inSimulation(simulation)
                .forEntity(lacz)
                .forMembrane(CELL_OUTER_MEMBRANE_REGION)
                .build();

        AutomatonNode node = simulation.getGraph().getNode(5, 1);
        double previousConcentration = UnitRegistry.concentration(10, MICRO_MOLE_PER_LITRE).getValue().doubleValue();
        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
            double currentConcentration = node.getConcentrationContainer().get(MEMBRANE, lacz);
            assertTrue(currentConcentration < previousConcentration);
        }

    }
}