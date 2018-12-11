package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.PitFormationRate;
import bio.singa.simulation.features.MaturationTime;
import bio.singa.simulation.features.VesicleRadius;
import bio.singa.simulation.model.agents.surfacelike.Membrane;
import bio.singa.simulation.model.agents.surfacelike.MembraneLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static tec.uom.se.unit.MetricPrefix.*;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
class ClathrinMediatedEndocytosisTest {

    @Test
    void shouldSimulateEndocytosis() {

        // setup simulation
        Simulation simulation = new Simulation();
        final double simulationExtend = 100;
        Rectangle rectangle = new Rectangle(simulationExtend, simulationExtend);
        simulation.setSimulationRegion(rectangle);
        ComparableQuantity<Length> systemExtend = Quantities.getQuantity(10, MICRO(METRE));
        Environment.setSystemExtend(systemExtend);
        Environment.setSimulationExtend(simulationExtend);
        UnitRegistry.setSpace(systemExtend);
        UnitRegistry.setTime(Quantities.getQuantity(1, MILLI(SECOND)));

        // setup graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setPosition(new Vector2D(50.0, 50.0));
        node.setCellRegion(CellRegion.MEMBRANE);
        simulation.setGraph(graph);

        // setup membrane
        List<Membrane> membranes = MembraneTracer.regionsToMembrane(graph);
        MembraneLayer membraneLayer = new MembraneLayer();
        membraneLayer.addMembranes(membranes);
        simulation.setMembraneLayer(membraneLayer);

        // setup species for clathrin decay
        ChemicalEntity clathrinHeavyChain = new Protein.Builder("Clathrin heavy chain")
                .assignFeature(new UniProtIdentifier("Q00610"))
                .build();

        ChemicalEntity clathrinLightChain = new Protein.Builder("Clathrin light chain")
                .assignFeature(new UniProtIdentifier("P09496"))
                .build();

        ComplexedChemicalEntity clathrinTriskelion = ComplexedChemicalEntity.create("Clathrin Triskelion")
                .addAssociatedPart(clathrinHeavyChain, 3)
                .addAssociatedPart(clathrinLightChain, 3)
                .build();

        // setup endocytosis budding
        ClathrinMediatedEndocytosis budding = new ClathrinMediatedEndocytosis();
        budding.setSimulation(simulation);
        budding.addMembraneCargo(Quantities.getQuantity(31415.93, new ProductUnit<Area>(NANO(METRE).pow(2))), 60.0, clathrinTriskelion);
        budding.setFeature(PitFormationRate.DEFAULT_BUDDING_RATE);
        budding.setFeature(VesicleRadius.DEFAULT_VESICLE_RADIUS);
        budding.setFeature(MaturationTime.DEFAULT_MATURATION_TIME);
        simulation.getModules().add(budding);

        while (simulation.getElapsedTime().isLessThanOrEqualTo(Quantities.getQuantity(200, SECOND))) {
            simulation.nextEpoch();
        }

        assertFalse(simulation.getVesicleLayer().getVesicles().isEmpty());
    }


}