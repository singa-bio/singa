package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.mathematics.graphs.model.GridCoordinateConverter;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.concentrations.SimpleConcentrationContainer;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author cl
 */
public class AutomatonGraphsTest {

    @Test
    public void shouldSplitGraph() {
        final int numberOfNodes = 11;
        AutomatonGraph rectangularAutomatonGraph = AutomatonGraphs.createRectangularAutomatonGraph(numberOfNodes, numberOfNodes);
        GridCoordinateConverter converter = new GridCoordinateConverter(numberOfNodes, numberOfNodes);
        EnclosedCompartment innerSection = new EnclosedCompartment("Right", "Right Compartment");
        EnclosedCompartment outerSection = new EnclosedCompartment("Left", "Left Compartment");
        AutomatonGraphs.splitRectangularGraphWithMembrane(rectangularAutomatonGraph, converter, innerSection, outerSection);
        // check correct assignment
        // right part
        CellSection right = rectangularAutomatonGraph.getCellSection("Right");
        assertEquals(55, right.getContent().size());
        for (AutomatonNode bioNode : right.getContent()) {
            assertEquals(bioNode.getState(), NodeState.AQUEOUS);
            assertTrue(bioNode.getConcentrationContainer() instanceof SimpleConcentrationContainer);
        }
        // left part
        CellSection left = rectangularAutomatonGraph.getCellSection("Left");
        assertEquals(55, left.getContent().size());
        for (AutomatonNode bioNode : left.getContent()) {
            assertEquals(bioNode.getState(), NodeState.AQUEOUS);
            assertTrue(bioNode.getConcentrationContainer() instanceof SimpleConcentrationContainer);
        }
        // left part
        CellSection membrane = rectangularAutomatonGraph.getCellSection("Right-M");
        assertEquals(11, membrane.getContent().size());
        for (AutomatonNode bioNode : membrane.getContent()) {
            assertEquals(bioNode.getState(), NodeState.MEMBRANE);
            assertTrue(bioNode.getConcentrationContainer() instanceof MembraneContainer);
        }


    }



}