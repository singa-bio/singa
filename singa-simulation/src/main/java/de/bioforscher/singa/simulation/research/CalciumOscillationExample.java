package de.bioforscher.singa.simulation.research;

import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.model.SimulationExamples;

/**
 * @author cl
 */
public class CalciumOscillationExample {

    public static void main(String[] args) {

        Simulation simulation = SimulationExamples.createSimulationFromSBML();

        Species x = new Species.Builder("X").build();

        int epoch = 1;
        while (epoch < 50000) {
            simulation.nextEpoch();
            if (simulation.getEpoch() % 100 == 0 && simulation.getEpoch() > 1) {
                System.out.println(simulation.getElapsedTime().getValue()+","+simulation.getGraph().getNode(0).getConcentration(x).getValue());
            }
            epoch = simulation.getEpoch();
        }

    }

}
