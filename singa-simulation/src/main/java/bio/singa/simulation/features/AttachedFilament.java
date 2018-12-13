package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;

/**
 * @author cl
 */
public class AttachedFilament extends AbstractFeature<LineLikeAgent.FilamentType> {

    private static final String SYMBOL = "filament";

    public AttachedFilament(LineLikeAgent.FilamentType filamentType, Evidence evidence) {
        super(filamentType, evidence);
    }

    @Override
    public String getDescriptor() {
        return SYMBOL;
    }
}
