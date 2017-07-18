package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.mathematics.graphs.model.GridCoordinateConverter;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
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
        CellSection right = rectangularAutomatonGraph.getSection("Right");
        assertEquals(55, right.getContent().size());
        for (BioNode bioNode : right.getContent()) {
            assertEquals(bioNode.getState(), NodeState.AQUEOUS);
            assertTrue(bioNode.getConcentrations() instanceof SimpleConcentrationContainer);
        }
        // left part
        CellSection left = rectangularAutomatonGraph.getSection("Left");
        assertEquals(55, left.getContent().size());
        for (BioNode bioNode : left.getContent()) {
            assertEquals(bioNode.getState(), NodeState.AQUEOUS);
            assertTrue(bioNode.getConcentrations() instanceof SimpleConcentrationContainer);
        }
        // left part
        CellSection membrane = rectangularAutomatonGraph.getSection("Right-M");
        assertEquals(11, membrane.getContent().size());
        for (BioNode bioNode : membrane.getContent()) {
            assertEquals(bioNode.getState(), NodeState.MEMBRANE);
            assertTrue(bioNode.getConcentrations() instanceof MembraneContainer);
        }


    }



}