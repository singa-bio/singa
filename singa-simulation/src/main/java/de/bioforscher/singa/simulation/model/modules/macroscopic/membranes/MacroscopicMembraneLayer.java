package de.bioforscher.singa.simulation.model.modules.macroscopic.membranes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MacroscopicMembraneLayer {

    private List<MacroscopicMembrane> membranes;

    public MacroscopicMembraneLayer() {
        membranes = new ArrayList<>();
    }

    public MacroscopicMembraneLayer(List<MacroscopicMembrane> membranes) {
        this.membranes = membranes;
    }

    public List<MacroscopicMembrane> getMembranes() {
        return membranes;
    }

    public void addMembrane(MacroscopicMembrane membrane) {
        membranes.add(membrane);
    }

}
