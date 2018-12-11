package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.simulation.model.agents.linelike.MicrotubuleOrganizingCentre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author cl
 */
public class MembraneLayer {

    private List<Membrane> membranes;

    private MicrotubuleOrganizingCentre microtubuleOrganizingCentre;

    public MembraneLayer() {
        membranes = new ArrayList<>();
    }

    public MicrotubuleOrganizingCentre getMicrotubuleOrganizingCentre() {
        return microtubuleOrganizingCentre;
    }

    public void setMicrotubuleOrganizingCentre(MicrotubuleOrganizingCentre microtubuleOrganizingCentre) {
        this.microtubuleOrganizingCentre = microtubuleOrganizingCentre;
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
