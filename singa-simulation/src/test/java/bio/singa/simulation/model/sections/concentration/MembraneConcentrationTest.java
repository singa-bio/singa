package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.model.Evidence;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.agents.surfacelike.MembraneTracer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Area;

import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellRegions.EARLY_ENDOSOME_VESICLE_REGION;
import static bio.singa.simulation.model.sections.CellTopology.MEMBRANE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
class MembraneConcentrationTest {

    private static Simulation simulation;
    private static ChemicalEntity entity;

    @BeforeAll
    static void initialize() {
        simulation = new Simulation();
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(graph);
        // entity
        entity = SmallMolecule.create("entity").build();
        // vesicles
        VesicleLayer vesicles = new VesicleLayer(simulation);
        Vesicle vesicle = new Vesicle(new Vector2D(20.0, 20.0), Quantities.getQuantity(50, NANO(METRE)));
        vesicle.setRegion(EARLY_ENDOSOME_VESICLE_REGION);
        vesicles.addVesicle(vesicle);
        simulation.setVesicleLayer(vesicles);
        MembraneTracer.regionsToMembrane(graph);
        // inititalize
        simulation.initializeSpatialRepresentations();
    }

    @Test
    void testNodeMembraneInitialization() {
        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(new MembraneConcentration(CELL_OUTER_MEMBRANE_REGION, entity,
                Quantities.getQuantity(1, MICRO(METRE).pow(2)).asType(Area.class), 1000, Evidence.NO_EVIDENCE));
        ci.initialize(simulation);
        assertEquals(1.6605390404271641E-12, simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(MEMBRANE, entity));
    }

    @Test
    void testVesicleMembraneInitialization() {
        ConcentrationInitializer ci = new ConcentrationInitializer();
        ci.addInitialConcentration(new MembraneConcentration(EARLY_ENDOSOME_VESICLE_REGION, entity,
                Quantities.getQuantity(1, MICRO(METRE).pow(2)).asType(Area.class), 1000, Evidence.NO_EVIDENCE));
        ci.initialize(simulation);
        assertEquals(5.2167372504050236E-14, simulation.getVesicleLayer().getVesicles().iterator().next().getConcentrationContainer().get(MEMBRANE, entity));
    }

}