package bio.singa.simulation.model.agents.surfacelike;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author cl
 */
public class MembraneLayer {

    private List<Membrane> membranes;

    public MembraneLayer() {
        membranes = new ArrayList<>();
    }

    public List<Membrane> getMembranes() {
        return membranes;
    }

    public void addMembrane(Membrane membrane) {
        membranes.add(membrane);
    }

    public void addMembranes(Collection<Membrane> membranes) {
        this.membranes.addAll(membranes);
    }

}
