package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;

/**
 * @author cl
 */
public class AttachedFilament extends AbstractFeature<LineLikeAgent.FilamentType> {

    private static final String SYMBOL = "filament";

    public AttachedFilament(LineLikeAgent.FilamentType filamentType, FeatureOrigin featureOrigin) {
        super(filamentType, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
