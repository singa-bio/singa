package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.model.SimulationExamples;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class DynamicReactionTest {

    private static final Logger logger = LoggerFactory.getLogger(DynamicReactionTest.class);

    @Test
    @Ignore
    public void shouldPerformCalciumOscialtionExample() {
        // this is a known problem and fixed in another branch
        logger.info("Performing dynamic reaction test ...");
        Simulation simulation = SimulationExamples.createSimulationFromSBML();

        Species x = new Species.Builder("X").build();
        BioNode node = simulation.getGraph().getNodes().iterator().next();

        while (simulation.getElapsedTime().getValue().doubleValue() <= 11400) {
            simulation.nextEpoch();
            if (simulation.getElapsedTime().getValue().doubleValue() == 1600.0) {
                assertEquals(0.022263980930189168, node.getConcentration(x).getValue().doubleValue(), 0.0);
                logger.info("Reached first reference point at {}.", simulation.getElapsedTime());
            }
            if (simulation.getElapsedTime().getValue().doubleValue() == 5500) {
                assertEquals(0.6053815562285324, node.getConcentration(x).getValue().doubleValue(), 0.0);
                logger.info("Reached second reference point at {}.", simulation.getElapsedTime());
            }
            if (simulation.getElapsedTime().getValue().doubleValue() == 7400) {
                assertEquals(0.02200581293506816, node.getConcentration(x).getValue().doubleValue(), 0.0);
                logger.info("Reached third reference point at {}.", simulation.getElapsedTime());
            }
            if (simulation.getElapsedTime().getValue().doubleValue() == 11400) {
                assertEquals(0.6035679498866569, node.getConcentration(x).getValue().doubleValue(), 0.0);
                logger.info("Reached fourth reference point at {}.", simulation.getElapsedTime());
            }
        }

        logger.info("Reached all reference points successfully.");
    }

}