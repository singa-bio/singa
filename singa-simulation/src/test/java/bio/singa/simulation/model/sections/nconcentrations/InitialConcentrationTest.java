package bio.singa.simulation.model.sections.nconcentrations;

import bio.singa.chemistry.entities.simple.SmallMolecule;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonGraphs;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.concentration.ConcentrationInitializer;
import bio.singa.simulation.model.sections.concentration.SectionConcentration;
import bio.singa.simulation.model.simulation.Simulation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.NANO_MOLE_PER_LITRE;
import static bio.singa.simulation.model.sections.CellRegions.CELL_OUTER_MEMBRANE_REGION;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class InitialConcentrationTest {

    private static SmallMolecule entity;

    @BeforeAll
    static void initialize() {
        // entity
        entity = SmallMolecule.create("entity").build();
    }

    @Test
    @DisplayName("concentration initialization - subsection initialization")
    void testNodeInitialization() {
        // setup test simulation
        Simulation simulation = new Simulation();
        // graph
        AutomatonGraph graph = AutomatonGraphs.singularGraph();
        AutomatonNode node = graph.getNode(0, 0);
        node.setCellRegion(CELL_OUTER_MEMBRANE_REGION);
        simulation.setGraph(graph);

        ConcentrationInitializer ci = new ConcentrationInitializer();
        ComparableQuantity<MolarConcentration> expected = Quantities.getQuantity(10, NANO_MOLE_PER_LITRE);
        ci.addInitialConcentration(new SectionConcentration(CELL_OUTER_MEMBRANE_REGION, CELL_OUTER_MEMBRANE_REGION.getInnerSubsection(), entity, expected));
        ci.initialize(simulation);
        assertEquals(expected, UnitRegistry.concentration(simulation.getGraph().getNode(0, 0).getConcentrationContainer().get(CellTopology.INNER, entity)).to(expected.getUnit()));
    }

}