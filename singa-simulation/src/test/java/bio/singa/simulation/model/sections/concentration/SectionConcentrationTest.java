package bio.singa.simulation.model.sections.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static bio.singa.simulation.model.sections.CellRegions.EARLY_ENDOSOME_VESICLE_REGION;
import static org.junit.jupiter.api.Assertions.*;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author cl
 */
class SectionConcentrationTest {

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
        Vesicle vesicle = new Vesicle(new Vector2D(20.0, 20.0), Quantities.getQuantity(20, NANO(METRE)));
        vesicle.setRegion(EARLY_ENDOSOME_VESICLE_REGION);
        simulation.setVesicleLayer(vesicles);
        // inititalize
        simulation.initializeSpatialRepresentations();
    }

    @Test
    @DisplayName("concentration initialization - subsection initialization")
    void testNodeInitialization() {
        ConcentrationInitializer ci = new ConcentrationInitializer();
        ComparableQuantity<MolarConcentration> expected = Quantities.getQuantity(10, NANO_MOLE_PER_LITRE);
        ci.addInitialConcentration(new SectionConcentration(CELL_OUTER_MEMBRANE_REGION, CELL_OUTER_MEMBRANE_REGION.getInnerSubsection(), entity, expected));
        ci.initialize(simulation);
        assertEquals(expected, UnitRegistry.concentration(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CellTopology.INNER, entity)).to(expected.getUnit()));
    }


}