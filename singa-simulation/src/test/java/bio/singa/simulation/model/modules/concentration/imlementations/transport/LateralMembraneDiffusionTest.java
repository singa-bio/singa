package bio.singa.simulation.model.modules.concentration.imlementations.transport;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.diffusivity.MembraneDiffusivity;
import bio.singa.chemistry.features.diffusivity.SaffmanDelbrueckDiffusivityCorrelation;
import bio.singa.chemistry.features.structure3d.Radius;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.surfacelike.MembraneBuilder;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static bio.singa.features.units.UnitProvider.MICRO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellRegions.CYTOPLASM_REGION;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;

/**
 * @author cl
 */
class LateralMembraneDiffusionTest {

    private static final Quantity<Length> systemDiameter = Quantities.getQuantity(2500.0, NANO(METRE));

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @BeforeEach
    void initializeEach() {
        Environment.setSimulationExtend(1100);
        Environment.setSystemExtend(systemDiameter);
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
        Environment.reset();
    }

    @Test
    void testInContext() {

        // create simulation
        Simulation simulation = new Simulation();
        // set node distance to diameter
        Environment.setNodeSpacingToDiameter(systemDiameter, 11);
        // create grid graph 11x11
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(11, 1);
        // set graph
        simulation.setGraph(graph);

        Protein lacz = Protein.create("LacZ")
                .assignFeature(new Radius(Quantities.getQuantity(3, NANO(METRE))))
                .build();

        // split with membrane
        MembraneBuilder.linear()
                .vectors(new Vector2D(0, 50), new Vector2D(1000, 50))
                .innerPoint(new Vector2D(500, 300))
                .graph(graph)
                .membraneRegion(CYTOPLASM_REGION, CELL_OUTER_MEMBRANE_REGION)
                .build();

        // set concentrations
        AutomatonNode node = graph.getNode(5, 0);
        node.getConcentrationContainer().initialize(CellTopology.MEMBRANE, lacz, Quantities.getQuantity(1, MICRO_MOLE_PER_LITRE));

        SaffmanDelbrueckDiffusivityCorrelation correlation = new SaffmanDelbrueckDiffusivityCorrelation();
        MembraneDiffusivity diffusivity = correlation.predict(lacz);
        System.out.println(diffusivity.getContent().to(Diffusivity.SQUARE_MICROMETRE_PER_SECOND));
        lacz.setFeature(diffusivity);

        LateralMembraneDiffusion.inSimulation(simulation)
                .onlyFor(lacz)
                .build();

        for (int i = 0; i < 10; i++) {
            simulation.nextEpoch();
        }

        System.out.println(UnitRegistry.humanReadable(node.getConcentrationContainer().get(CellTopology.MEMBRANE, lacz)));

    }
}