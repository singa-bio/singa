package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.RegionFeature;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Simulation;

/**
 * @author cl
 */
public class ContainmentRegion extends RegionFeature {

    public ContainmentRegion(CellRegion region) {
        super(region);
    }

    public static Builder of(CellRegion quantity) {
        return new Builder(quantity);
    }

    public VolumeLikeAgent retrieveAreaAgent(Simulation simulation) {
        for (VolumeLikeAgent agent : simulation.getVolumeLayer().getAgents()) {
            if (agent.getCellRegion().equals(getContent())) {
                return agent;
            }
        }
        throw new IllegalStateException("There exists no region in the simulation matching the given region: " + getContent());
    }

    public static class Builder extends AbstractFeature.Builder<CellRegion, ContainmentRegion, Builder> {

        public Builder(CellRegion quantity) {
            super(quantity);
        }

        @Override
        protected ContainmentRegion createObject(CellRegion quantity) {
            return new ContainmentRegion(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
