package de.bioforscher.singa.simulation.model.modules.macroscopic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MembraneLayer {

    private List<MacroscopicMembrane> membranes;

    public MembraneLayer() {
        membranes = new ArrayList<>();
    }

    public MembraneLayer(List<MacroscopicMembrane> membranes) {
        this.membranes = membranes;
    }

    public List<MacroscopicMembrane> getMembranes() {
        return membranes;
    }

    public void addMembrane(MacroscopicMembrane membrane) {
        membranes.add(membrane);
    }

}
