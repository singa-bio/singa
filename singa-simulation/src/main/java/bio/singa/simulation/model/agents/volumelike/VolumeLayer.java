package bio.singa.simulation.model.agents.volumelike;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class VolumeLayer {

    private List<VolumeLikeAgent> agents;

    public VolumeLayer() {
        agents = new ArrayList<>();
    }

    public void addAgent(VolumeLikeAgent agent) {
        agents.add(agent);
    }

    public List<VolumeLikeAgent> getAgents() {
        return agents;
    }
}
