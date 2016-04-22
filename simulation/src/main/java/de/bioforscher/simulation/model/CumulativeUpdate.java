package de.bioforscher.simulation.model;

public interface CumulativeUpdate extends Update {

    void updateConcentration(AutomatonGraph graph);

}