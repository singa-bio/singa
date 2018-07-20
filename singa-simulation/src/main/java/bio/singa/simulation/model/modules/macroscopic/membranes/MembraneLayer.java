package bio.singa.simulation.model.modules.macroscopic.membranes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MembraneLayer {

    private List<Membrane> membranes;

    public MembraneLayer() {
        membranes = new ArrayList<>();
    }

    public MembraneLayer(List<Membrane> membranes) {
        this.membranes = membranes;
    }

    public List<Membrane> getMembranes() {
        return membranes;
    }

    public void addMembrane(Membrane membrane) {
        membranes.add(membrane);
    }

}
