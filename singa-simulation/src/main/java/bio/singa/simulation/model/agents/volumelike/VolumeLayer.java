package bio.singa.simulation.model.agents.volumelike;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class VolumeLayer {

    private ActinCortex cortex;
    private List<VolumeLikeAgent> agents;

    public VolumeLayer() {
        agents = new ArrayList<>();
    }

    public ActinCortex getCortex() {
        return cortex;
    }

    public void setCortex(ActinCortex cortex) {
        this.cortex = cortex;
    }

    public void addAgent(VolumeLikeAgent agent) {
        this.agents.add(agent);
    }

    public List<VolumeLikeAgent> getAgents() {
        return agents;
    }
}
