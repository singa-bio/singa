package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;

import java.util.List;

/**
 * @author cl
 */
public class ContainmentRegion extends RegionFeature {

    public ContainmentRegion(CellRegion region, List<Evidence> evidence) {
        super(region, evidence);
    }

    public ContainmentRegion(CellRegion region, Evidence evidence) {
        super(region, evidence);
    }

    public ContainmentRegion(CellRegion region) {
        super(region);
    }

    public VolumeLikeAgent retrieveAreaAgent(Simulation simulation) {
        for (VolumeLikeAgent agent : simulation.getVolumeLayer().getAgents()) {
            if (agent.getCellRegion().equals(getContent())) {
                return agent;
            }
        }
        throw new IllegalStateException("There exists no region in the simulation matching the given region: " + getContent());
    }

}
